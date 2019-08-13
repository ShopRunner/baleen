workflow "New workflow" {
  on = "push"
  resolves = ["Gradle Check"]
}

action "Gradle Check" {
  uses = "actions/setup-java@v1.0.0"
  args = "./gradlew check"
}
