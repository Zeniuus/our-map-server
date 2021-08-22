variable "vpc_id" {
  default = "vpc-3163f05a" // ap-northeast-2
}

variable "default_subnet_ids" {
  default = [
    "subnet-13d45878",
    "subnet-9ac1bfe1",
    "subnet-00a1514f",
    "subnet-1c14d643"
  ]
}

data "aws_vpc" "default" {
  id = var.vpc_id
}
