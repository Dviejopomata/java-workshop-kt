package com.es.eoi.shop

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@SpringBootApplication
class ShopApplication

@Service
class Warehouse {

    var articles: MutableList<Article> = mutableListOf<Article>()

}

fun <T> List<T>.replace(newValue: T, block: (T) -> Boolean): List<T> {
    return map {
        if (block(it)) newValue else it
    }
}

@Service
class ArticleManager(@Autowired private var warehouse: Warehouse) : Manageable {

    override fun save(article: Article) {
        warehouse.articles.add(article)
    }

    override fun delete(deleteArticle: Article) {
        warehouse.articles.remove(deleteArticle)
    }

    override fun read(idArticle: String): Article {
        return warehouse.articles.first { art -> art.id == idArticle }
    }

    override fun listAll(): List<Article> {
        return warehouse.articles
    }

    override fun update(article: Article) {
        warehouse.articles.replace(article, { art -> art.id == article.id })
    }

}

interface Prizable {
    fun getTotalPrice(): Double
}

interface Manageable {

    fun save(article: Article)
    fun delete(article: Article)
    fun read(idArticle: String): Article
    fun listAll(): List<Article>
    fun update(article: Article)

}


open class Article : Prizable {

    var name: String? = null
    var price: Double = 0.0
    var vat: String? = null
    var stock: Int? = null
    var id: String? = null
    var category: String? = null
    override fun getTotalPrice(): Double {
        println("CALCULADO CON IVA DEFAULT")
        return this.price * DEFAULT_IVA
    }

    companion object {
        private val DEFAULT_IVA = 1.21
    }


}

class Textile : Article() {
    val TEXTILE_VAR: Double = 1.21
    var color: String? = null
    var size: String? = null
    override fun getTotalPrice(): Double {
        return price * TEXTILE_VAR
    }

    companion object {
        private val TEXTILE_VAR = 1.21
    }
}

class Food : Article() {
    var expirationDate: String? = null
    var allergens: String? = null

    override fun getTotalPrice(): Double {
        return this.price * FOOD_VAT
    }

    companion object {
        private val FOOD_VAT = 1.10
    }

}

object ArticleFactory {

    fun getArticle(category: String): Article {

        var article: Article?
        when (category) {
            "textil" -> article = Textile()
            "electronica" -> article = Electronics()
            "alimentacion" -> article = Food()
            else -> throw Exception("El tipo $category no es aceptado")
        }
        return article

    }

}

class Electronics : Article() {
    var power: String? = null
    var guarantee: Double? = null

    override fun getTotalPrice(): Double {
        return this.price * ELECTRONICS_VAT
    }

    companion object {
        private val ELECTRONICS_VAT = 1.21
    }

}


object Menu {

    internal var scan = Scanner(System.`in`)

    fun printMenu() {

        println("BIENVENIDO A MI TIENDA, INTRODUZCA UNA OPCION")
        println("1-COMPRAR ARTICULOS")
        println("2-LISTAR ARTICULOS")
        println("3-GESTIONAR ARTICULOS")


        val option = scan.nextInt()

        println("Has elegido la opcion: $option")
        println("")

        when (option) {
            1 -> {
            }
            2 -> listArticles()
            3 -> {
            }

            else -> {
            }
        }

        // printMenu();

    }

    private fun listArticles() {

        println("Introduce el id del articulo a listar:")
        val id = scan.next()

//        val article = Main.articleManager.read(id)

//        println("Producto: " + article.name!!)
        //System.out.println("Stock" + article.getStock());
        //System.out.println("Precio: " + article.getPrice() + "€");
        //System.out.println("Precio con IVA " + article.getTotalPrice());

    }


}

// MAIN ANTIGUO
//object Main {
//
//    var warehouse = Warehouse(100)
//    var articleManager = ArticleManager(warehouse)
//
//    @JvmStatic
//    fun main(args: Array<String>) {
//
//        initArticles() // Inicializamos articulos
//        Menu.printMenu()
//    }
//
//    private fun initArticles() {
//
//        val scan = Scanner(System.`in`)
//        var buffer = ""
//
//        val article = mutableListOf<Article>()
//
//        for (i in 0..2) {
//            article.add(ArticleFactory.getArticle("textil"))
//        }
//
//        for (i in 0..0) {
//            println("Introduce el nombre del articulo:")
//            buffer = scan.next()
//            article[i].name = buffer
//
//            println("Introduce el id")
//            buffer = scan.next()
//            article[i].id = buffer
//        }
//
//        warehouse.articles = article
//
//    }
//}

@RestController
class ShopController {
    @Autowired
    private lateinit var articleManager: ArticleManager

    @GetMapping()
    fun get(): String {
        return "OK"
    }

    @GetMapping("articles")
    fun getArticulos(): List<Article> {
        return articleManager.listAll()
    }
}

fun main(args: Array<String>) {
    runApplication<ShopApplication>(*args)
}
