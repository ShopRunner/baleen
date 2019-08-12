workflow "New workflow" {
  on = "push"
  resolves = ["Gradle Check"]
}

action "Gradle Check" {
  uses = "actions/setup-java@232795a7c4c518061ce6a41f418b171de03cb907"
  runs = "./gradlew check"
}
