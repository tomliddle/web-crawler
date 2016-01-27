
WEBCRAWLER

I used Scala to get the benefits of immutability and functional benefits such as the collection functions.
Akka was used to avoid any concurrency issues.
I used a pattern called the Work Pulling pattern to avoid overloading any of the actors queues with job requests.
This pattern highly scaleable and should be able to crawl websites containing thousands of pages fast.
With not many changes, more worker threads could be added, even across multiple machines.

The design:
The WebCrawler actor starts with a queue containing the url of the page to be crawled.
The WebCrawler has a number of worker actors which request work to the WebCrawler
The WebCrawler responds by sending a url from the queue to the Worker actor who requested it and also adding the url to
	an "attempted" set so it isn't attempted twice.
The Worker fetches the resource at the url and parses it for local links, images and external links and puts them in a
	Page class and sends it back to the WebCrawler.
If the Worker fails to get the requested resource it sends back a Failed message with the url. This is in an attempt to
	keep track of the open requests which is currently used for debugging
The WebCrawler then adds this page to the map of pages, and prints out the results to std out.
The WebCralwer adds any new links to the queue and sends out a work available message. Workers then respond again with
	another RequestWork message and the process repeats until all links are followed.



