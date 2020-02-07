package main

import (
	"net/smtp"
)

// smtpServer data to smtp server
type smtpServer struct {
	host string
	port string
}

// Address URI to smtp server
func (s *smtpServer) Address() string {
	return s.host + ":" + s.port
}

func sendEmail(emailAddress, message string) error {
	from := "emovent2e@gmail.com"
	password := "datwftuzeuooijom"
	to := emailAddress

	//prepare hosting server
	_smtpServer := smtpServer{host: "smtp.gmail.com", port: "587"}

	msg := "From: " + "Ahmed Hesham" + "\n" +
		"To: " + to + "\n" +
		"Subject: Test\n\n" +
		message

	//Authintication
	auth := smtp.PlainAuth("", from, password, _smtpServer.host)

	//Sending Email
	err := smtp.SendMail(_smtpServer.Address(), auth, from, []string{to}, []byte(msg))

	return err
}
