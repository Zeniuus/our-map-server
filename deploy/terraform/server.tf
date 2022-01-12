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

resource "aws_ecr_repository" "frontend_admin" {
  name                 = "our-map-frontend-admin"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_key_pair" "swann" {
  key_name   = "swann"
  public_key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQD14Q8XXmOyQzgze0ThU8hYQjf/ATyT5t9gr4RsfCc2Br9zZs11iz469NW/HFbALo0ON31hMznP25HPoNfvLKlx4W22wt26ebHs3aWSUZIUtlSMfiT3eOjk0p1i3hWSwlfT5tz8H6DtYGteehExVS8YWcSDt5+gnzvebIB04YV2RjDFOdVI3woumGoC1zAHSWp+huek62KlIPxhGOjrzu1iyV98EcA8HLhUGeP8Cn+BBID5h6veUPWrmzS1pkfxdwApz0XLLqQNtywWBJt2OeB8/ydgs5oL4PJEVIe8jIaZnyEF6P10G9yF5CYm+chbu1Ltm4LhlI31whhaM/EiibAz"
}
