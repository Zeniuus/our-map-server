terraform {
  backend "s3" {
    bucket = "suhwan.dev-terraform-backend"
    key    = "our-map-ecs"
    region = "ap-northeast-2"
  }
}
