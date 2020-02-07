package main

import (
	"context"
	"encoding/base64"
	"encoding/json"
	"fmt"
	"net/http"
	"strings"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
)

func registerHandler(w http.ResponseWriter, r *http.Request) {
	if strings.ToLower(r.Method) != "post" {
		return
	}

	name, user, email, password := strings.TrimSpace(r.Header.Get("name")), strings.TrimSpace(r.Header.Get("user")), strings.TrimSpace(r.Header.Get("email")), r.Header.Get("password")
	switch true {
	case isEmbtyString(name):
	case isEmbtyString(user):
	case isEmbtyString(email):
	case isEmbtyString(password):
		w.WriteHeader(http.StatusForbidden)
		fmt.Fprint(w, "complete the fields first")
		return
	}
	if !isValidEmail(email) {
		w.WriteHeader(http.StatusForbidden)
		fmt.Fprint(w, "Invalid Email Address")
		return
	}

	fmt.Println(name, user, email, password)

	client, err := mongo.Connect(context.TODO(), clientOptions)
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		return
	}

	type fFilter struct {
		email string
		user  string
	}
	type infoStat struct {
		Email int
		User  int
	}

	var _fFilter fFilter
	_infoStat := infoStat{}
	collection := client.Database("flow").Collection("users")

	userFilter := bson.D{primitive.E{Key: "user", Value: user}}
	collection.FindOne(context.TODO(), userFilter).Decode(&_fFilter)
	fmt.Println(11)
	if !isEmbtyString(_fFilter.user) {
		_infoStat.User = 1
	}
	userFilter = bson.D{primitive.E{Key: "email", Value: email}}
	collection.FindOne(context.TODO(), userFilter).Decode(&_fFilter)
	fmt.Println(22)
	if !isEmbtyString(_fFilter.email) {
		_infoStat.Email = 1
	}

	//0=embty, 1=not embty
	switch _infoStat {
	case infoStat{1, 0}, infoStat{0, 1}, infoStat{1, 1}:
		w.WriteHeader(http.StatusForbidden)
		fmt.Println(_infoStat)
		js, err := json.Marshal(_infoStat)
		if err != nil {
			w.WriteHeader(http.StatusInternalServerError)
			r.Body.Close()
			return
		}
		w.Write(js)
		fmt.Println(string(js))
		return
	}

	bHashPW, bSalt, err := genHash(password)
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
	PW, salt := base64.StdEncoding.EncodeToString(bHashPW), base64.StdEncoding.EncodeToString(bSalt)
	upload := userInfo{name, user, email, PW, salt, "", false, "", "", ""}
	_, err = collection.InsertOne(context.TODO(), upload)
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		return
	}

	fmt.Fprint(w, "confirm your email")
}
