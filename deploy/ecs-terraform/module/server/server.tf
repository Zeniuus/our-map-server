data "aws_iam_policy_document" allow_assume_role_from_ecs {
  version = "2012-10-17"
  statement {
    sid     = ""
    effect  = "Allow"
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}


resource "aws_iam_role" ecs_task_execution_role {
  name               = "${var.server_name}-ecs-execution-role"
  assume_role_policy = data.aws_iam_policy_document.allow_assume_role_from_ecs.json
}

resource "aws_iam_role_policy_attachment" ecs_task_execution_role {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role_policy" ecs_task_execution_role_secretsmanager {
  role   = aws_iam_role.ecs_task_execution_role.id
  policy = <<EOT
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "secretsmanager:GetSecretValue"
      ],
      "Resource": [
        "${var.secretsmanager_secret_arn}"
      ]
    }
  ]
}
EOT
}

resource "aws_ecs_service" server {
  name = var.server_name
  cluster = var.ecs_cluster_id
  task_definition = aws_ecs_task_definition.server.arn
  desired_count = 1
  launch_type = "FARGATE"

  network_configuration {
    security_groups = [aws_security_group.server.id]
    subnets = var.default_vpc_subnet_ids
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.server.id
    container_name = var.server_name
    container_port = var.container_port
  }
}

resource "aws_ecs_task_definition" server {
  family = var.server_name
  requires_compatibilities = ["FARGATE"]
  network_mode = "awsvpc"
  execution_role_arn = aws_iam_role.ecs_task_execution_role.arn
  cpu = var.cpu
  memory = var.memory
  container_definitions = <<EOT
[
  {
    "name": "${var.server_name}",
    "image": "${var.image_name}",
    "cpu": ${var.cpu},
    "memory": ${var.memory},
    "essential": true,
    "portMappings": [
      {
        "containerPort": ${var.container_port},
        "hostPort": ${var.container_port}
      }
    ],
    "logConfiguration": {
      "logDriver": "awslogs",
      "options": {
        "awslogs-group": "${var.server_name}",
        "awslogs-region": "ap-northeast-2",
        "awslogs-stream-prefix": "ecs"
      }
    },
    "environment": ${var.ecs_task_definition_environment},
    "secrets": ${var.ecs_task_definition_secrets}
  }
]
EOT

  runtime_platform {
    operating_system_family = "LINUX"
    cpu_architecture        = "X86_64"
  }
}

resource "aws_cloudwatch_log_group" server {
  name = var.server_name
}

resource "aws_security_group" server {
  vpc_id = var.default_vpc_id
  name = "${var.server_name}-sg"
}

resource "aws_security_group_rule" server_ingress_lb {
  security_group_id = aws_security_group.server.id
  type              = "ingress"
  protocol          = "tcp"
  from_port         = 0
  to_port           = var.container_port
  source_security_group_id = var.lb_security_group_id
}

resource "aws_security_group_rule" server_egress_all {
  security_group_id = aws_security_group.server.id
  type              = "egress"
  protocol          = "-1"
  from_port         = 0
  to_port           = 0
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" db_ingress_server {
  security_group_id = var.db_security_group_id
  type              = "ingress"
  protocol          = "tcp"
  from_port         = 3306
  to_port           = 3306
  source_security_group_id = aws_security_group.server.id
}

resource "aws_lb_target_group" server {
  name = "${var.server_name}-lb-tg"
  port = var.container_port
  protocol = "HTTP"
  vpc_id = var.default_vpc_id
  target_type = "ip" # ECS service의 target group이 되려면 instance type이 아닌 ip type이 되어야 한다.

  health_check {
    path = "/health"
    matcher = "200-299"
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_lb_listener_rule" server {
  listener_arn = var.lb_listener_arn
  priority = var.lb_listener_rule_priority

  action {
    type = "forward"
    target_group_arn = aws_lb_target_group.server.arn
  }

  condition {
    host_header {
      values = [var.lb_listener_rule_host_header]
    }
  }

  condition {
    path_pattern {
      values = [var.lb_listener_rule_path_pattern]
    }
  }
}

// TODO: state 이전 필요
#resource "aws_ecr_repository" server {
#  name                 = ${var.server_name}
#  image_tag_mutability = "MUTABLE"
#
#  image_scanning_configuration {
#    scan_on_push = true
#  }
#}