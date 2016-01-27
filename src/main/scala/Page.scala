/**
  * A web page with its url, pages and links
  *
  * @param url
  * @param pages
  * @param staticContent
  */
case class Page(url: String, pages: Set[String], staticContent: Set[String], externalUrls: Set[String]) {
	override def toString = s"$url \nPages: $pages \n\nImages: $staticContent \n\nExternal links: $externalUrls\n"
}
