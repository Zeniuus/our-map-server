module "our_map_prod_server" {
  source = "./module/server"

  server_name = "our-map-prod-server"
  default_vpc_id = var.default_vpc_id
  default_vpc_subnet_ids = var.default_vpc_subnet_ids
  ecs_cluster_id = aws_ecs_cluster.prod.id
  lb_security_group_id = aws_security_group.our_map_lb.id
  lb_listener_arn = aws_lb_listener.https.arn
  lb_listener_rule_host_header = "api.staircrusher.club"
  lb_listener_rule_priority = 100
  ecs_task_definition_environment = <<EOT
[
  {
    "name": "OUR_MAP_OVERRIDING_PROPERTIES_FILENAMES",
    "value": "/app/conf/secret.properties"
  }
]
EOT
  ecs_task_definition_secrets = <<EOT
[
  {
    "name": "BASE64_ENC_SECRET_PROPS_FILE",
    "valueFrom": "${aws_secretsmanager_secret.our_map_prod_server_base64_enc_secret_props_file.arn}"
  }
]
EOT
  image_name = "563057296362.dkr.ecr.ap-northeast-2.amazonaws.com/our-map-server:latest"
  container_port = 8080
  cpu = 256
  memory = 1024
  secretsmanager_secret_arn = aws_secretsmanager_secret.our_map_prod_server_base64_enc_secret_props_file.arn
  db_security_group_id = var.db_security_group_id
}

module "our_map_prod_server_admin" {
  source = "./module/server"

  server_name = "our-map-prod-server-admin"
  default_vpc_id = var.default_vpc_id
  default_vpc_subnet_ids = var.default_vpc_subnet_ids
  ecs_cluster_id = aws_ecs_cluster.prod.id
  lb_security_group_id = aws_security_group.our_map_lb.id
  lb_listener_arn = aws_lb_listener.https.arn
  lb_listener_rule_host_header = "admin.staircrusher.club"
  lb_listener_rule_path_pattern = "/api/*"
  lb_listener_rule_priority = 99 # frontend_admin 보다는 앞으로 오도록
  ecs_task_definition_environment = <<EOT
[
  {
    "name": "OUR_MAP_OVERRIDING_PROPERTIES_FILENAMES",
    "value": "/app/conf/secret.properties"
  }
]
EOT
  ecs_task_definition_secrets = <<EOT
[
  {
    "name": "BASE64_ENC_SECRET_PROPS_FILE",
    "valueFrom": "${aws_secretsmanager_secret.our_map_prod_server_base64_enc_secret_props_file.arn}"
  }
]
EOT
  image_name = "563057296362.dkr.ecr.ap-northeast-2.amazonaws.com/our-map-server-admin:latest"
  container_port = 8081
  cpu = 256
  memory = 512
  secretsmanager_secret_arn = aws_secretsmanager_secret.our_map_prod_server_base64_enc_secret_props_file.arn
  db_security_group_id = var.db_security_group_id
}

module "our_map_prod_frontend_admin" {
  source = "./module/server"

  server_name = "our-map-prod-fe-admin"
  default_vpc_id = var.default_vpc_id
  default_vpc_subnet_ids = var.default_vpc_subnet_ids
  ecs_cluster_id = aws_ecs_cluster.prod.id
  lb_security_group_id = aws_security_group.our_map_lb.id
  lb_listener_arn = aws_lb_listener.https.arn
  lb_listener_rule_host_header = "admin.staircrusher.club"
  lb_listener_rule_path_pattern = "/*"
  lb_listener_rule_priority = 101
  ecs_task_definition_secrets = <<EOT
[
  {
    "name": "BASE64_ENC_CONFIG_FILE",
    "valueFrom": "${aws_secretsmanager_secret.our_map_prod_frontend_base64_enc_config_file.arn}"
  }
]
EOT
  image_name = "563057296362.dkr.ecr.ap-northeast-2.amazonaws.com/our-map-frontend-admin:latest"
  container_port = 80
  cpu = 256
  memory = 512
  secretsmanager_secret_arn = aws_secretsmanager_secret.our_map_prod_frontend_base64_enc_config_file.arn
  db_security_group_id = var.db_security_group_id
}

resource "aws_secretsmanager_secret" our_map_prod_server_base64_enc_secret_props_file {
  name = "our-map-prod-server-base64-enc-secret-props-file"
}

resource "aws_secretsmanager_secret" our_map_prod_frontend_base64_enc_config_file {
  name = "our-map-prod-frontend-base64-enc-config-file"
}

variable "db_security_group_id" {
  default = "sg-0736e281337244ace"
}
