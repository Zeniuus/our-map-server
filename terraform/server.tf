resource "aws_iam_instance_profile" "server" {
  name = "our_map_server_profile"
  role = aws_iam_role.server.name
}

resource "aws_iam_role" "server" {
  name = "our_map_server_role"
  path = "/"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "ec2.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF

  inline_policy {
    name = "allow_ecr_access"

    policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "ecr:GetAuthorizationToken",
        "ecr:GetDownloadUrlForLayer",
        "ecr:BatchGetImage",
        "ecr:BatchCheckLayerAvailability",
        "ecr:PutImage",
        "ecr:InitiateLayerUpload",
        "ecr:UploadLayerPart",
        "ecr:CompleteLayerUpload"
      ],
      "Effect": "Allow",
      "Resource": "*"
    }
  ]
}
EOF
  }
}

resource "aws_instance" "server" {
  ami           = "ami-0b827f3319f7447c6" // ap-northeast-2
  instance_type = "t2.micro"

  iam_instance_profile = aws_iam_instance_profile.server.id

  key_name = "swann"

  vpc_security_group_ids = [aws_security_group.server.id]
}

resource "aws_eip" "server" {
  instance = aws_instance.server.id
  vpc      = true
}

resource "aws_security_group" "server" {
  name        = "server"
  description = "server instance sg"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description      = "Allow server traffics from LB"
    from_port        = 80
    to_port          = 80
    protocol         = "tcp"
    security_groups  = [aws_security_group.server_lb.id]
  }

  ingress {
    description      = "Allow server-admin traffics from LB"
    from_port        = 8081
    to_port          = 8081
    protocol         = "tcp"
    security_groups  = [aws_security_group.server_lb.id]
  }

  ingress {
    description      = "Allow SSH connections"
    from_port        = 22
    to_port          = 22
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

resource "aws_ecr_repository" "server" {
  name                 = "our-map-server"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_ecr_repository" "server_admin" {
  name                 = "our-map-server-admin"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_key_pair" "swann" {
  key_name   = "swann"
  public_key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQD14Q8XXmOyQzgze0ThU8hYQjf/ATyT5t9gr4RsfCc2Br9zZs11iz469NW/HFbALo0ON31hMznP25HPoNfvLKlx4W22wt26ebHs3aWSUZIUtlSMfiT3eOjk0p1i3hWSwlfT5tz8H6DtYGteehExVS8YWcSDt5+gnzvebIB04YV2RjDFOdVI3woumGoC1zAHSWp+huek62KlIPxhGOjrzu1iyV98EcA8HLhUGeP8Cn+BBID5h6veUPWrmzS1pkfxdwApz0XLLqQNtywWBJt2OeB8/ydgs5oL4PJEVIe8jIaZnyEF6P10G9yF5CYm+chbu1Ltm4LhlI31whhaM/EiibAz"
}
