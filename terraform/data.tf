variable "vpc_id" {
//  default = "vpc-3163f05a" // ap-northeast-2
  default = "vpc-f0f7f288" // TODO: ap-northeast-2로 변경하기
}

data "aws_vpc" "default" {
  id = var.vpc_id
}
