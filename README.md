# AI Engineering Workshop
This repository acts as a starting point to get into engineering an exemplary system which utilizes LLMs as
parts of applications.

We're using https://docs.langchain4j.dev/intro to integrate a Spring Boot application with OpenAI.

There are a lot of things that exist in this repository already. This is necessary to allow us to get to the core
of the challenge of working with LLMs rather than spending all of our time constructing a use-case.

I have distributed various hints and questions throughout the code-base in the form of `// TODO:` comments.

## Setup
* Install SDKMan - https://sdkman.io/
* Run `sdk env install`
* `cp .env.example .env` and add you OPENAI API Key
* Use `SPRING_PROFILES_ACTIVE=local` for `gradle bootRun` in the IDE of your choice or the terminal
* Start WebUI using `docker compose up`