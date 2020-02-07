package main

import (
	"fmt"
	"io"
	"net/http"
	"os"
)

func uploadPost(w http.ResponseWriter, r *http.Request) {
	err := r.ParseMultipartForm(200000)
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		return
	}

	formData := r.MultipartForm
	files := formData.File["multiplefiles"]

	for _, file := range files {

		fs, err := file.Open()
		if err != nil {
			w.WriteHeader(http.StatusInternalServerError)
			return
		}
		defer fs.Close()

		os.Mkdir("./tmp/", os.ModePerm)
		out, err := os.Create("./tmp/" + generateRandomStr(32))
		defer out.Close()
		if err != nil {
			w.WriteHeader(http.StatusInternalServerError)
			return
		}

		_, err = io.Copy(out, fs)
		if err != nil {
			w.WriteHeader(http.StatusInternalServerError)
			return
		}

	}

	fmt.Fprintf(w, "Files uploaded successfully : ")

}
