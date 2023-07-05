# Contributing to gRPC-Metrics

First off, thank you for taking some time to contribute to this awesome library. It is great to have others make this project even more awesome.
The following is a set of guidelines for contributing to the gRPC metrics library. If at any point you feel like this guide needs to be improved, feel free to propose a change to it as well.

## What should I know before I get started?

gRPC Metrics is an interceptor library for both client and server side applications. It allows Prometheus to scrape gRPC related metrics for your application. This library is framework agnostic and should work with any Java 11 application regardless of implementation. This library heavily relies on Prometheus client and Apache gRPC dependencies.

## How can I contribute?

### Reporting a bug
Before reporting a bug:
* Check if it has been reported as an issue
* Make sure to mention which tag of the library you are using
* 
In order to report a bug, create an issue in the project repository. Issues must include:
* High level description of the issue
* What is the current behavior
* What should be the intended behavior
* Current impact
* How to reproduce the issue
* Include any sort of proof or material that can facilitate tracking and fixing the bug
    * Screenshots
    * Code block
    * Link to gist

### Code Contributions
All contributions must follow all guidelines presented here, so be sure to read this document thoroughly before contributing. All code changes must:
* Come in the form of a pull request
* Must contain in the PR a brief and clear description of
    * What is being changed/added
    * Why is this being change being proposed
    * Include screenshots (if applicable)
* All pipeline steps must succeed
* Must include new unit tests or changes to existing ones depending on the case 
    * Code coverage must not drop below 75%
    * If there is a drastic reduction in code coverage, explain why
* Must be tested in at least one existing service. This test includes:
    * Running this service in with the proposed change in lower environment
    * Service used for testing must pass all tests
* Changes to existing features or addition of new features must include a screenshot as proof of change (if applicable)
* Must be approved by at least **two** code owners/maintainers
* Must squash commits when merging
* Example MR:
```
Why is this being proposed

Added a metric to count all gRPC errors to facilitate SRE and developers' lives in figuring out if there are any issues and making alerting rules easier.
Changed metric naming so it is simpler
Added CONTRIBUTING.md so everyone is aware of how to contribute to this project


What changed

Updated README with new examples
"app" is no longer the metrics namespace

"gRPC" is the new metric namespace
"server" is a possible subsystem
"client" is a possible subsystem


Added count for failed gRPC calls (anything with status different from OK)
Added CONTRIBUTING.md
Set gRPC API dependency scope to provided so library user doesn't have to handle dependency issues from their side


Tests
New metric with new naming convention after testing it in Compendium by calling the getGames method

# HELP grpc_server_failed_count Total number of RPCs that failed
# TYPE grpc_server_failed_count counter
grpc_server_failed_count_total{servicename="compendium",grpc_service="sony.cgei.compendium.proto.game.GameApi",grpc_method="getGames",grpc_type="SERVER_STREAMING",} 1.0
grpc_server_failed_count_created{servicename="compendium",grpc_service="sony.cgei.compendium.proto.game.GameApi",grpc_method="getGames",grpc_type="SERVER_STREAMING",} 1.622220870888E9

```
### Suggesting enhancements
Suggestions are always appreciated, especially if they come in the form of a pull request. In case you don't have the bandwidth to work on that change yourself, you must:
* Create a issue story ticket in the repository
    * Description of how this project can be improved
    * Examples of what should happen after the change
    * Include screenshots or illustrations (if applicable)
* It is worth noting that:
    * Suggesting it doesn't mean it will get implemented. Idea must be approved by owners and maintainers first
    * Be available to discuss the idea in further detail if required
    * Implementation by a maintainer or owner may take time since it requires availability. If this is critical, consider proposing the code change yourself first.

## Releasing a new version
Before releasing a version, make sure:
* You are using the Maven plugin
* All pipeline steps have succeeded in the master branch
* Follow the [Semantic Versioning](https://semver.org/) guide when naming the version
* Include release notes after tag has been cut



