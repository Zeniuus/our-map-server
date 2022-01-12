terraform {
  backend "s3" {
    bucket = "suhwan.dev-terraform-backend"
    key    = "our-map"
    region = "ap-northeast-2"
  }
}
