import akka.actor.{Actor, Props, ActorSystem}
import org.slf4j.LoggerFactory

import scala.collection.immutable.Queue


object WebCrawler {
	def main(args: Array[String]): Unit = {
		val system = ActorSystem("WebCrawler")
		system.actorOf(Props(new Master(100, "http://www.wipro.com")))
	}
}


class WebCrawler(noOfWorkers: Int, url: String) extends Actor {

	private val log = LoggerFactory.getLogger(getClass)
	private val workers  = (1 to noOfWorkers).map(_ => context.actorOf(Props(new Worker(url))))
	private var pages = Map[String, Page]()
	private var queue = Queue[String](url)
	private var attemptedUrls = Set[String]()
	private var active = 0

	def receive = {
		// We retrieve a parsed page from the worker thread. Add to
		case page: Page =>
			active = active - 1
			log.debug(s"active count is now $active")

			// Add the page to the map
			pages = pages + (page.url -> page)

			// Print the results to std out
			println(s"$page\n")

			// Now add the links to the queue to search if we haven't already added it
			page.pages.foreach {
				pageUrl =>
					if (!attemptedUrls.contains(pageUrl)) {
						queue = queue.enqueue(pageUrl)
						workers foreach { _ ! WorkAvailable }
					}
			}

		// Work failed, count down the tries
		case WorkFailed(url) =>
			active = active - 1

		case RequestWork =>
			queue.dequeueOption match {
				case Some(queueElem) =>
					attemptedUrls = attemptedUrls + queueElem._1
					queue = queueElem._2
					sender ! Work(queueElem._1)
					active = active + 1
					log.debug(s"parsing ${queueElem._1} active count is now $active")
				case None => None
			}

	}

}
