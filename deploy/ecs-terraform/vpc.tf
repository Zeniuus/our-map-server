variable "default_vpc_id" {
  default = "vpc-3163f05a" // ap-northeast-2
}

data "aws_vpc" "default" {
  id = var.default_vpc_id
}

variable "default_vpc_subnet_ids" {
  default = ["subnet-13d45878", "subnet-00a1514f"]
}
