variable "server_name" {
  description = "Should add \"our-map\" prefix manually."
}

variable "default_vpc_id" {}

variable "default_vpc_subnet_ids" {}

variable "ecs_cluster_id" {}

variable "lb_security_group_id" {}

variable "lb_listener_arn" {}

variable "lb_listener_rule_priority" {}

variable "lb_listener_rule_host_header" {}

variable "lb_listener_rule_path_pattern" {
  default = "/*"
}

variable "ecs_task_definition_environment" {
  default = "[]"
}

variable "ecs_task_definition_secrets" {
  default = "[]"
}

variable "image_name" {}

variable "container_port" {}

variable "cpu" {}

variable "memory" {}

variable "secretsmanager_secret_arn" {}

variable "db_security_group_id" {}
