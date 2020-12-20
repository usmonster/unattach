Unattach
========

See [homepage](https://unattach.app/) for more info.

Legal
-------
* [LICENSE](LICENSE)
* [PRIVACY](PRIVACY)

Dependencies
------------
* Install [Java 14](https://www.oracle.com/java/technologies/javase-downloads.html).
* Install [Maven](https://maven.apache.org/download.cgi).

Build & Run
-----------
* Build with `mvn clean package`.
* Run with `java -jar target/client-3.0.0-jar-with-dependencies.jar`.

Known Limitations
-----------------
* On some emails, the app will fail with `OutOfMemoryError` even with the maximum heap size set to 2GB. This occurs
  when the Gmail API client library unpacks the downloaded email in local memory using a third-party JSON library, which
  appears to sometimes make inefficient use of the available memory. If this happens, the original email will remain
  intact, the memory will be recovered, and the processing will continue with the next email.
* The maximum number of search results is 500 despite the requested limit being much higher. This appears to be a
  restriction within the Gmail API service. A potential workaround would be to apply date ranges in automatic follow-up
  requests. For now, this workaround can be done manually, by modifying the query in the Advanced view. If removing
  attachments, re-running the search will work, since the app will find a different set of (up to) 500 emails.

Contributions
-------------
Feel free to
[report issues](https://help.github.com/en/articles/creating-an-issue) and
[create pull requests](https://help.github.com/en/articles/creating-a-pull-request).

Support This Project
--------------------
If you like Unattach, [Buy Developers a Coffee](https://unattach.app/#support) ☕

You can also [Become My Sponsor](https://github.com/sponsors/rokstrnisa) ❤️
