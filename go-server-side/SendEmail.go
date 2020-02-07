package main

import (
	"net/smtp"
)

type smtpServer struct {
	host string
	port string
}

// Address URI to smtp server
func (s *smtpServer) Address() string {
	return s.host + ":" + s.port
}

//sendEmail is a function to send emails to client
//emailAddress is the client's email, subject is the subject of the email and message is the message to send and it could be an html file
func sendEmail(emailAddress, subject, message string) error {
	from := "emovent.sup@gmail.com"
	password := "zetfxupqlpeucdux"
	to := emailAddress

	//prepare hosting server
	_smtpServer := smtpServer{host: "smtp.gmail.com", port: "587"}

	msg := "From: " + "Emovent\"" + "\n" +
		"To: " + to + "\n" +
		"Subject: " + subject + "\n\n" +
		message

	//Authintication
	auth := smtp.PlainAuth("Emovent", from, password, _smtpServer.host)

	//Sending Email
	err := smtp.SendMail(_smtpServer.Address(), auth, from, []string{to}, []byte(msg))

	return err
}
