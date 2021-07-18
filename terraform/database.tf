resource "random_password" "password" {
  length           = 32
  special          = true
  override_special = "_%@"
}

resource "aws_db_instance" "default" {
  name                 = "our_map"
  instance_class       = "db.t2.micro"
  port                 = 3306

  engine               = "mysql"
  engine_version       = "5.7.34"
  parameter_group_name = "default.mysql5.7"

  storage_type         = "gp2"
  allocated_storage    = 20

  vpc_security_group_ids = [aws_security_group.db.id]

  username             = "root"
  password             = random_password.password.result
  skip_final_snapshot  = true
}

resource "aws_security_group" "db" {
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
