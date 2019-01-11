package scrap.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Main {
	public static String MERCARI_URL_FILE = "C:\\Users\\nobu\\Desktop\\tmp\\mercari_url.txt";
	public static String OUTPUT_HTML_FILE = "C:\\Users\\nobu\\Desktop\\tmp\\output_mercari.html";

	public static void main(String[] args) {

		// メルカリのURL読み込み
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(MERCARI_URL_FILE), StandardCharsets.UTF_8));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		String str;
		Document document = null;
		Elements element = null;
		String html = "";
		try {
			while ((str = reader.readLine()) != null) {
				// 先頭が半角シャープ"#"であるコメント行はスキップする
				if (str.startsWith("#")) {
					continue;
				}

				int pageNo = 1;
				// 販売中の商品が存在しないURLまでループする
				do {
					// ページのソースを取得、リクエストのタイムアウトは10秒に設定
					document = Jsoup
							.connect(str + "?page=" + pageNo + "#sell-items")
							.timeout(10000).get();
					element = document.getElementsByClass("entertainment-product-sell-item-content");
					html += element.outerHtml();
					pageNo++;
				} while (!element.isEmpty());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// htmlファイル作成
		try {
			File file = new File(OUTPUT_HTML_FILE);
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			pw.println("<!DOCTYPE html>");
			pw.println("<html lang=\"ja-JP\">");
			pw.println("<head>");
			pw.println("<link href=\"https://item.mercari.com/jp/assets/css/app.jp.css?3062056556\" rel=\"stylesheet\">");
			pw.println("</head>");
			pw.println("<body>");
			pw.println("<main class=\" l-container clearfix\">");
			pw.println(html.replaceAll("class=\"lazyload\"", "").replaceAll("data-src", "src"));
			pw.println("</main>");
			pw.println("</body>");
			pw.println("</html>");
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
