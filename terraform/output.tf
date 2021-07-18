output "aws_eip_address" {
  value = aws_eip.server.public_ip
}

output "db_password" {
  value = random_password.password.result
}
