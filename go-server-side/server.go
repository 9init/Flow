package main

import (
	"fmt"
	"net/http"
	"strings"

	"go.mongodb.org/mongo-driver/mongo/options"
)

type userInfo struct {
	Name        string
	User        string
	Email       string
	Password    string
	Salt        string
	BlockedIP   string
	Confirmed   bool
	PhoneNumber string
	DateOfBirth string
	_id         string
}
type IPInfo struct {
	_id        string
	IP         string
	LastTry    string
	TryCounter int
	Blocked    bool
}

const (
	saltBytes   = 32
	pwHashBytes = 64
)

var clientOptions = options.Client().ApplyURI("mongodb://localhost:27017/Emovent/Flow")

func main() {
	http.HandleFunc("/register", registerHandler)
	http.HandleFunc("/check", func(w http.ResponseWriter, r *http.Request) {
		if strings.ToLower(r.Method) == "post" {
			w.WriteHeader(http.StatusOK)
			return
		}
		w.WriteHeader(404)

	})
	http.HandleFunc("/login", loginHandler)

	fmt.Println("running")
	err := http.ListenAndServe(":8080", nil)
	if err != nil {
		panic(err)
	}
}

//-----------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------
// ----------------------------------------------------------------------------------------
// ----------------------------------------------------------------------------------------
// ----------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------
