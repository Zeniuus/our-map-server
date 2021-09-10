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
}

resource "aws_security_group_rule" "server_lb_ingress_http" {
  security_group_id = aws_security_group.server_lb.id
  type              = "ingress"
  description       = "Allow HTTP requests"
  from_port         = 80
  to_port           = 80
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
}


resource aws_security_group_rule "server_lb_ingress_https" {
  security_group_id = aws_security_group.server_lb.id
  type              = "ingress"
  description       = "Allow HTTPS requests"
  from_port         = 443
  to_port           = 443
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
}

resource aws_security_group_rule "server_lb_egress_server" {
  security_group_id        = aws_security_group.server_lb.id
  type                     = "egress"
  description              = "Allow routing traffic to server"
  from_port                = 80
  to_port                  = 80
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.server.id
}

resource aws_security_group_rule "server_lb_egress_server_admin" {
  security_group_id        = aws_security_group.server_lb.id
  type                     = "egress"
  description              = "Allow routing traffic to server-admin"
  from_port                = 8081
  to_port                  = 8081
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.server.id
}

resource aws_security_group_rule "server_lb_egress_frontend_admin" {
  security_group_id        = aws_security_group.server_lb.id
  type                     = "egress"
  description              = "Allow routing traffic to frontend-admin"
  from_port                = 3001
  to_port                  = 3001
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.server.id
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

resource aws_lb_listener_rule "server_https_server_admin" {
  listener_arn = aws_lb_listener.server_https.arn
  priority = 10

  action {
    type = "forward"
    target_group_arn = aws_lb_target_group.server_admin_https.arn
  }

  condition {
    host_header {
      values = ["admin.staircrusher.club"]
    }
  }

  condition {
    path_pattern {
      values = ["/api/*"]
    }
  }
}

resource aws_lb_listener_rule "server_https_frontend_admin" {
  listener_arn = aws_lb_listener.server_https.arn
  priority = 100

  action {
    type = "forward"
    target_group_arn = aws_lb_target_group.frontend_admin_https.arn
  }

  condition {
    host_header {
      values = ["admin.staircrusher.club"]
    }
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

resource aws_lb_target_group "server_admin_https" {
  name     = "lb-tg-server-admin-https"
  port     = 8081
  protocol = "HTTP"
  vpc_id   = data.aws_vpc.default.id

  health_check {
    port     = 8081
    protocol = "HTTP"
    path     = "/health"
    matcher  = "200-299"
  }
}

resource "aws_lb_target_group_attachment" "server_admin_https" {
  target_group_arn = aws_lb_target_group.server_admin_https.arn
  target_id        = aws_instance.server.id
  port             = 8081
}

resource aws_lb_target_group "frontend_admin_https" {
  name     = "lb-tg-frontend-admin-https"
  port     = 3001
  protocol = "HTTP"
  vpc_id   = data.aws_vpc.default.id

  health_check {
    port     = 3001
    protocol = "HTTP"
    path     = "/health"
    matcher  = "200-299"
  }
}

resource "aws_lb_target_group_attachment" "frontend_admin_https" {
  target_group_arn = aws_lb_target_group.frontend_admin_https.arn
  target_id        = aws_instance.server.id
  port             = 3001
}

resource "aws_acm_certificate" "server_lb" {
  domain_name       = "*.staircrusher.club"
  validation_method = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}
