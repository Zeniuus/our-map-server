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
//  ami           = "ami-0b827f3319f7447c6" // ap-northeast-2
  ami           = "ami-0dc8f589abe99f538" // TODO: ap-northeast-2로 변경하기
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

  // TODO: 현재 도메인을 구매하지 않아서 SSL 인증서를 발급할 수 없음. 따라서 임시로 HTTPS가 아니라 HTTP 서버를 운영함.
  //       도메인 구매하면 AWS ACM Certificate + NLB TLS Termination 기능을 사용하면 될 것 같다.
  ingress {
    description      = "Allow HTTP requests"
    from_port        = 80
    to_port          = 80
    protocol         = "tcp"
    cidr_blocks      = ["0.0.0.0/0"]
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

resource "aws_key_pair" "swann" {
  key_name   = "swann"
  public_key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQD14Q8XXmOyQzgze0ThU8hYQjf/ATyT5t9gr4RsfCc2Br9zZs11iz469NW/HFbALo0ON31hMznP25HPoNfvLKlx4W22wt26ebHs3aWSUZIUtlSMfiT3eOjk0p1i3hWSwlfT5tz8H6DtYGteehExVS8YWcSDt5+gnzvebIB04YV2RjDFOdVI3woumGoC1zAHSWp+huek62KlIPxhGOjrzu1iyV98EcA8HLhUGeP8Cn+BBID5h6veUPWrmzS1pkfxdwApz0XLLqQNtywWBJt2OeB8/ydgs5oL4PJEVIe8jIaZnyEF6P10G9yF5CYm+chbu1Ltm4LhlI31whhaM/EiibAz"
}
