package main

import (
	"net/http"
	"strings"
)

func checkHandler(w http.ResponseWriter, r *http.Request) {
	if strings.ToLower(r.Method) == "post" {
		w.WriteHeader(http.StatusOK)
		return
	}
	w.WriteHeader(404)

}
