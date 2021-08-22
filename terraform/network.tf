resource aws_lb "server_lb" {
  name               = "server-lb"
  load_balancer_type = "application"
  security_groups    = [aws_security_group.server_lb.id]
  subnets            = var.default_subnet_ids

  enable_deletion_protection = true
}

resource "aws_security_group" "server_lb" {
  name        = "server_lb"
  description = "server lb sg"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description      = "Allow HTTP requests"
    from_port        = 80
    to_port          = 80
    protocol         = "tcp"
    cidr_blocks      = ["0.0.0.0/0"]
  }

  ingress {
    description      = "Allow HTTPS requests"
    from_port        = 443
    to_port          = 443
    protocol         = "tcp"
    cidr_blocks      = ["0.0.0.0/0"]
  }

  egress {
    description      = "Egress"
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }
}

resource aws_lb_listener "server_http" {
  load_balancer_arn = aws_lb.server_lb.id
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = "redirect"

    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}

resource aws_lb_listener "server_https" {
  load_balancer_arn = aws_lb.server_lb.id
  port              = 443
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = aws_acm_certificate.server_lb.arn

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.server_https.arn
  }
}

resource aws_lb_target_group "server_https" {
  name     = "lb-tg-server-https"
  port     = 80
  protocol = "HTTP"
  vpc_id   = data.aws_vpc.default.id

  health_check {
    port     = 80
    protocol = "HTTP"
    path     = "/health"
    matcher  = "200-299"
  }

  // 이게 있어야 replace가 정상적으로 동작한다.
  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_lb_target_group_attachment" "server_https" {
  target_group_arn = aws_lb_target_group.server_https.arn
  target_id        = aws_instance.server.id
  port             = 80
}

resource "aws_acm_certificate" "server_lb" {
  domain_name       = "api.staircrusher.club"
  validation_method = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}
