output "server_eip_address" {
  value = aws_eip.server.public_ip
}

output "server_lb_domain_name" {
  value = aws_lb.server_lb.dns_name
}

output "server_lb_cert_domain_validation_options" {
  value = aws_acm_certificate.server_lb.domain_validation_options
}
