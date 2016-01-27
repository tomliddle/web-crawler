
import org.jsoup.Jsoup
import org.scalatest.BeforeAndAfterEach
import org.scalatest.Matchers
import org.scalatest.WordSpec

class DocumentParserTest extends WordSpec with Matchers with BeforeAndAfterEach{

	val doc = Jsoup.parseBodyFragment(
		"""
						<a href="http://ktoso.github.io/scala-types-of-types/#phantom-type">phantom type</a>
					  	<a href="http://ktoso.github.io/scala-types-of-types/#phantom-type">phantom type</a>
					  	<a href="http://externalurl.com/scala-types-of-types/#phantom-type">phantom type</a>
		  				<a href="http://newurl.com/scala-types-of-types/#phantom-type">phantom type</a>
		  				<a href="http://ktoso.github.io/newlink/type">link type</a>
						<img src="/newimage.jpg" />
	  					<img src="/newimage2.jpg" />
	  					<img src="/newimage3.jpg" />
		""".stripMargin)
	val worker = new DocumentParser {
		val url = """http://ktoso.github.io"""
	}

	"MainClass" when {

		"getting external URLS" should {

			"get the 2 external urls" in {
				val urlSet = worker.getExternalURLs(doc)

				urlSet.size shouldBe(2)
				urlSet.contains("http://externalurl.com/scala-types-of-types/#phantom-type") shouldBe(true)
				urlSet.contains("http://newurl.com/scala-types-of-types/#phantom-type") shouldBe(true)
			}
		}

		"getting internal URLS" should {

			"get the 2 internal urls" in {
				val urlSet = worker.getInternalURLs(doc)

				urlSet.size shouldBe(2)
				urlSet.contains("http://ktoso.github.io/scala-types-of-types/#phantom-type") shouldBe(true)
				urlSet.contains("http://ktoso.github.io/newlink/type") shouldBe(true)
			}
		}

		"getting static content" should {

			"get the 3 images" in {
				val urlSet = worker.getStaticContentURLs(doc)

				urlSet.size shouldBe(3)
				urlSet.contains("/newimage.jpg") shouldBe(true)
				urlSet.contains("/newimage2.jpg") shouldBe(true)
				urlSet.contains("/newimage3.jpg") shouldBe(true)
			}
		}
	}
}