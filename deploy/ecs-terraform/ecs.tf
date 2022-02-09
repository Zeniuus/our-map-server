resource "aws_ecs_cluster" prod {
  name = "our-map-prod-ecs-cluster"
}

resource "aws_lb" our_map {
  name = "our-map-lb"
  subnets = var.default_vpc_subnet_ids
  load_balancer_type = "application"
  security_groups = [aws_security_group.our_map_lb.id]
}

resource "aws_lb_listener" http {
  load_balancer_arn = aws_lb.our_map.arn
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

resource "aws_lb_listener" https {
  load_balancer_arn = aws_lb.our_map.arn
  port              = 443
  protocol          = "HTTPS"
  certificate_arn   = data.aws_acm_certificate.star_staircrusher_club.arn
  ssl_policy        = "ELBSecurityPolicy-2016-08"

  default_action {
    type = "fixed-response"
    fixed_response {
      content_type = "text/html"
      status_code = "404"
    }
  }
}

resource "aws_security_group" our_map_lb {
  vpc_id = var.default_vpc_id
  name = "our-map-lb-sg"
}

resource "aws_security_group_rule" "our_map_lb_ingress_http" {
  security_group_id = aws_security_group.our_map_lb.id
  type              = "ingress"
  protocol          = "tcp"
  from_port         = 80
  to_port           = 80
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "our_map_lb_ingress_https" {
  security_group_id = aws_security_group.our_map_lb.id
  type              = "ingress"
  protocol          = "tcp"
  from_port         = 443
  to_port           = 443
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "our_map_lb_egress_all" {
  security_group_id = aws_security_group.our_map_lb.id
  type              = "egress"
  protocol          = -1
  from_port         = 0
  to_port           = 0
  cidr_blocks       = ["0.0.0.0/0"]
}

data aws_acm_certificate star_staircrusher_club {
  domain = "*.staircrusher.club"
}
