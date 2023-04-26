# Sample Api Test Demo
This project is incomplete and serves only as starter demo for a potential API automation test framework using:
- Java (JDK 17)
- Maven
- Junit 5

The publicly available, free, hosted apis for test chosen are as at: https://reqres.in/

# Getting Started
- Ensure JDK 17 or higher is installed (or modify the pom.xml - may be able to execute on lower versions)
- Ensure Maven is installed and configured - this project used version 3.8.1
- Make sure the executing machine has access to the public api under test: https://reqres.in/api/users

- Run:

| command                      | output                                                              |
|------------------------------|---------------------------------------------------------------------|
| `mvn test `                  | produces a .txt and an .xml report under `/target/surefire-reports` |
| `mvn surefire-report:report` | produces the above + a surefire .html `report under target/site`    |

* NOTE: a deliberately failing test is included so that the report can show more meaningful outcomes. 

## Status & Roadmap
Due to time limitations this project is paused and incomplete.   
Some key design decisions thus far are noted in the relevant code areas - java / pom files.  
In particular, the `UsersApiTestSuite` [UsersApiTestSuite](./src/test/java/reqres/UsersApiTestSuite.java) documents some of the test features.

There are many improvements and extensions I would add to the framework given time- highlights as follows:

### Framework Improvements
- Introduce **environment configuration** such as `dotenv` good practice for extracting environment variables to a .env file that is never committed to the repository
    - enhances security,
    - enables managing different environment dependencies as code,
    - and prepares the codebase for pipeline execution where such details are managed by the pipeline


- Introduce better **reporting** (the current use of surefire html is very limited) 
  - replace the LogWrapper implementation with fully integrated Junit5 logging that also preserves artifacts as output
  - output the request and response details as artifacts in addition to the test log for easy defect raising and triage support

  
- Introduce **pipeline**
  - Run on demand, with environment setup by the pipeline such a gitlab .yaml or Jenkinsfile for a hosted server
  - Include dashboard reporting
  - Show how test/data dependencies when required can be handled
  - Include security checks for library vulnerabilities, possibly code linting as well.
  

- Introduce **dynamic test input data support** - such as using template `xrequest.json` with a mustache library to support runtime values such as date time, unique user creation etc


### Code Improvements (lower level detail)
- Included as `TODO`s in codebase - such as:
  - Pre / Post test suite setup and teardown tasks to be included such as configuring logging
  - Specific values that should be extracted to an environment setup
  
### Sample Test Expansion
- Include examples of POST, PUT, DELETE tests
- Include authorisation examples if possible
- Include negative test examples
Build a couple of demo tests, include pipeline readiness (build, env extraction), structure for nested JSON, samples of non-trivial validations.


## Authors and Acknowledgment
Danae Milton   
With thanks to the [resources](https://reqres.in/) provided by [Ben Howdle](https://benhowdle.im/)

## License
Only open source libraries have been used in the making of this project.  



