package main

import "net/smtp"

// smtpServer data to smtp server
type smtpServer struct {
	host string
	port string
}

// Address URI to smtp server
func (s *smtpServer) Address() string {
	return s.host + ":" + s.port
}

func sendEmail(emailAddress, message string) (err error) {
	from, password := "emovent2e@gmail.com", "ahahmasgm011"
	to := []string{emailAddress}

	//prepare hosting server
	_smtpServer := smtpServer{host: "smtp.gmail.com", port: "587"}

	msg := []byte(message)

	//Authintication
	auth := smtp.PlainAuth("Emovent", from, password, _smtpServer.host)

	//Sending Email
	err = smtp.SendMail(_smtpServer.Address(), auth, from, to, msg)

	return
}
