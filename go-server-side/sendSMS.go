package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"net/url"
	"time"
)

type msgRes struct {
	Code          string
	SMSID         int
	Vodafone      int
	Etisalat      int
	Orange        int
	We            int
	Language      string
	Vodafone_cost int
	Etisalat_cost int
	Orange_cost   int
	We_cost       int
}

func sendSMS(mobile *string) {
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		msg := "Hello Hisham\nremember your niggas"
		t := time.Now().Add(2 * time.Minute).Format("2006-01-02-15-04")
		URL, err := url.Parse("https://smsmisr.com")
		URL.Path += "/api/webapi/"
		parameters := url.Values{}
		parameters.Add("username", "dxbMVQAz")
		parameters.Add("password", "Cr8MtpYUrr")
		parameters.Add("language", "1")
		parameters.Add("sender", "Emovent")
		parameters.Add("mobile", *mobile)
		parameters.Add("message", msg)
		parameters.Add("DelayUntil", t)
		URL.RawQuery = parameters.Encode()
		res, err := http.Post(URL.String(), "application/json", nil)
		if err != nil {
			panic(err)
		}
		defer res.Body.Close()
		var b msgRes
		err = json.NewDecoder(res.Body).Decode(&b)
		if err != nil {
			panic(err)
		}
		if b.Code == "1901" {
			fmt.Fprint(w, "Success, Message Submitted Successfully")
			fmt.Println(b.Code, "Success, Message Submitted Successfully")
		} else {
			fmt.Fprint(w, b)
			fmt.Println(b)
		}

	})
}
