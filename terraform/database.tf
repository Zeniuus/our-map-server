resource "random_password" "main_db_password" {
  length           = 32
  special          = true
  override_special = "_%@"
}

resource "aws_db_parameter_group" "main_db" {
  name   = "main-db"
  family = "mysql5.7"

  // 장소 / 건물 데이터 마이그레이션 파일의 사이즈가 기본 max_allowed_packet의 크기보다 커서 조정해준다.
  parameter {
    name  = "max_allowed_packet"
    value = "41943040"
  }

  parameter {
    name  = "character_set_server"
    value = "utf8mb4"
  }

  parameter {
    name  = "collation_server"
    value = "utf8mb4_unicode_ci"
  }
}

resource "aws_db_instance" "main_db" {
  name                 = "our_map"
  instance_class       = "db.t2.micro"
  port                 = 3306

  engine               = "mysql"
  engine_version       = "5.7.34"
  parameter_group_name = aws_db_parameter_group.main_db.name

  storage_type         = "gp2"
  allocated_storage    = 20

  vpc_security_group_ids = [aws_security_group.main_db.id]

  username             = "root"
  password             = random_password.main_db_password.result
  skip_final_snapshot  = true
}

resource "aws_security_group" "main_db" {
  name        = "db"
  description = "db instance sg"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description      = "Allow server connection"
    from_port        = 3306
    to_port          = 3306
    protocol         = "tcp"
    security_groups  = [aws_security_group.server.id]
  }

  egress {
    description      = "Egress"
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }
}
