import WebCrawler.{WorkFailed, Work, WorkAvailable, RequestWork}
import akka.actor.Actor
import org.jsoup.Jsoup
import org.jsoup.nodes.{Element, Document}
import org.slf4j.LoggerFactory
import scala.concurrent.Future
import scala.util.{Try, Failure, Success}
import scala.collection.JavaConversions._

/**
  * Fetches the resource from the url given in the work message. Sends a message to the parent with the parsed links
  *
  */
class Worker extends Actor with DocumentParser {
	implicit val ec = context.dispatcher
	private val log = LoggerFactory.getLogger(getClass)

	override def preStart {
		context.parent ! RequestWork
	}

	def receive = {
		case WorkAvailable =>
			context.parent ! RequestWork

		case Work(url: String) =>
			doWork(url).map {
				_ match {
					// A successful retrieval, notify the master
					case Success(p) =>
						context.parent ! p
						context.parent ! RequestWork
					// A failure notify the master also so we can count down the requests
					case Failure(e) =>
						context.parent ! WorkFailed(url)
				}
			}
	}

	/**
	  * Fetches the url and parses the html for relevant elements
	  *
	  * @param url
	  * @return Future Try of the page
	  */
	def doWork(url: String): Future[Try[Page]] = {
		Future {
			Try {
				val doc = Jsoup.connect(url).get()

				val internal = getInternalURLs(doc, url)
				val external = getExternalURLs(doc, url)
				val images = getStaticContentURLs(doc, url)

				Page(url, internal, images, external)
			}
		}
	}
}

/**
  * Functionality to parse a document object and return sets of URLS.
  * Extracted to allow easy testing.
  */
trait DocumentParser {

	def getInternalURLs(doc: Document, url: String): Set[String] = {
		doc.select("a[href]").toSet[Element].map(elem => elem.attr("abs:href")).filter(_.startsWith(url))
	}

	def getStaticContentURLs(doc: Document, url: String): Set[String] = {
		doc.select("img").toSet[Element].map(elem => elem.attr("src"))
	}

	def getExternalURLs(doc: Document, url: String): Set[String] = {
		doc.select("a[href]").toSet[Element].map(elem => elem.attr("abs:href")).filterNot(_.startsWith(url))
	}

}