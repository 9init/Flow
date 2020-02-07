package main

import (
	"context"
	"encoding/base64"
	"encoding/json"
	"fmt"
	"net"
	"net/http"
	"strconv"
	"strings"
	"time"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
	"golang.org/x/crypto/scrypt"
)

func loginHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("ok login")
	if strings.ToLower(r.Method) != "post" {
		return
	}

	client, err := mongo.Connect(context.TODO(), clientOptions)
	if err != nil {
		fmt.Fprintln(w, "something went wrong with server")
	}
	collection := client.Database("flow").Collection("users")

	//Finding Nemo

	//Seting up filter
	uInput := r.Header.Get("uInput")
	password := r.Header.Get("password")

	if isEmbtyString(uInput) || isEmbtyString(password) {
		w.WriteHeader(http.StatusForbidden)
		fmt.Fprint(w, "complete the fields")
		return
	}

	// Here we go
	var filter interface{}
	if isValidEmail(uInput) {
		filter = bson.D{primitive.E{Key: "email", Value: uInput}}
	} else {
		filter = bson.D{primitive.E{Key: "user", Value: uInput}}
	}

	var compareAble userInfo
	err = collection.FindOne(context.TODO(), filter).Decode(&compareAble)
	if err != nil {
		switch err {
		case mongo.ErrNoDocuments:
			w.WriteHeader(http.StatusForbidden)
			fmt.Fprint(w, "user not found")
			return
		default:
			w.WriteHeader(http.StatusInternalServerError)
			return
		}
	}

	//We found the user! :D
	//we'll hash the password to auth
	userSalt, err := base64.StdEncoding.DecodeString(compareAble.Salt)
	if err != nil {
		panic(err)
	}
	userPass := compareAble.Password
	blockedIP := compareAble.BlockedIP

	ip, _, _ := net.SplitHostPort(r.RemoteAddr)

	_id := strings.TrimSpace(ip + compareAble._id)
	IPCollection := client.Database("flow").Collection("ip")
	var userIP IPInfo
	err = IPCollection.FindOne(context.TODO(), bson.D{primitive.E{Key: "_id", Value: _id}}).Decode(&userIP)
	if err != nil {
		switch err {
		case mongo.ErrNoDocuments:
			userIP = IPInfo{_id, ip, "", 0, false}
			_, err = IPCollection.InsertOne(context.TODO(), userIP)
			if err != nil {
				w.WriteHeader(http.StatusInternalServerError)
				return
			}
		default:
			w.WriteHeader(http.StatusInternalServerError)
			return
		}

	}

	tryCounter := userIP.TryCounter
	if tryCounter == 10 {
		//check the time from blocking
		lastTry, _ := strconv.Atoi(userIP.LastTry)
		timeNow, _ := strconv.Atoi(time.Now().Format("20060102150405"))

		blockedIPList := strings.Split(blockedIP, ",")
		for _, IPinList := range blockedIPList {
			if IPinList == ip {
				if lastTry > timeNow {
					w.WriteHeader(http.StatusForbidden)
					fmt.Fprint(w, "your ip is blocked for 24h")
					return
				}
				tryCounter = 0
				break
			}
		}
	}

	fmt.Println(4)

	hash, err := scrypt.Key([]byte(password), userSalt, 32768, 8, 1, pwHashBytes)

	if err != nil {
		panic(err)
	}

	hashString := base64.StdEncoding.EncodeToString(hash)

	xx, _ := base64.StdEncoding.DecodeString(userPass)
	fmt.Println(string(hash), string(xx))

	fmt.Println("\n\n", compareAble)
	if hashString == userPass {
		//loged in
		update := bson.D{primitive.E{Key: "$set", Value: bson.D{primitive.E{Key: "trycounter", Value: 0}, primitive.E{Key: "lasttry", Value: 0}}}}
		IPCollection.UpdateOne(context.TODO(), filter, update)

		var dataMap *map[string]interface{}
		jsonByte, _ := json.Marshal(compareAble)
		json.Unmarshal(jsonByte, &dataMap)
		delete(*dataMap, "passW")
		delete(*dataMap, "salt")
		delete(*dataMap, "_id")
		delete(*dataMap, "lastTry")
		delete(*dataMap, "tryCounter")
		jsonByte, _ = json.Marshal(dataMap)
		w.Write(jsonByte)
		fmt.Println("logged in")
	} else {
		//Wrong password
		tryCounter++
		if tryCounter == 10 {
			now := time.Now()
			t := addHours(now, 24)
			blockedIP += "," + ip
			updateUser := bson.D{primitive.E{Key: "$set", Value: bson.D{primitive.E{Key: "blockedip", Value: blockedIP}}}}
			updateIP := bson.D{primitive.E{Key: "$set", Value: bson.D{primitive.E{Key: "trycounter", Value: 10}, primitive.E{Key: "lasttry", Value: t}}}}

			_, err := collection.UpdateOne(context.TODO(), filter, updateUser)
			if err != nil {
				w.WriteHeader(http.StatusInternalServerError)
				return
			}
			_, err = IPCollection.UpdateOne(context.TODO(), filter, updateIP)
			if err != nil {
				w.WriteHeader(http.StatusInternalServerError)

				return
			}
			w.WriteHeader(http.StatusForbidden)
			fmt.Fprint(w, "your ip is blocked for 24H")
			return
		}
		updateIP := bson.D{primitive.E{Key: "$set", Value: bson.D{primitive.E{Key: "trycounter", Value: tryCounter}, primitive.E{Key: "lasttry", Value: 0}}}}
		collection.UpdateOne(context.TODO(), filter, updateIP)
		w.WriteHeader(http.StatusForbidden)
		fmt.Fprint(w, "Wrong password")
	}
}
