output "aws_eip_address" {
  value = aws_eip.server.public_ip
}

output "db_password" {
  value = random_password.main_db_password.result
}
