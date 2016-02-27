package models

import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db.DB

/**
  * Created by asus on 26.2.2016.
  */

case class Account(id: Int, email: String, password: String, name: String, role: Role)

object Account {

  val defaultParser = int("id") ~ str("email") ~ str("password") ~ str("name") ~ str("role") map {
    case id ~ email ~ pass ~ name ~ role => Account(id, email, pass, name, NormalUser)
  }

  def authenticate(email: String, password: String): Option[Account] =
    findByEmail(email).filter { account => account.password == password }

  def findByEmail(email: String): Option[Account] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM Accounts WHERE email={email}")
      .on('email -> email).executeQuery().singleOpt(defaultParser)
  }

  def findById(id: Int): Option[Account] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM Accounts WHERE id={id}")
      .on('id -> id).executeQuery().singleOpt(defaultParser)
  }

  def create(email: String, password: String, name: String, role: Role) = DB.withConnection { implicit c =>
    SQL("INSERT INTO BOOKMARKS(email, password, name, role) VALUES ({email}, {password}, {name}, {role});")
      .on('email -> email, 'password -> password, 'name -> name, 'role -> role.toString)
      .executeInsert()
  }

  def findAll: Seq[Account] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM Accounts;").executeQuery().list(defaultParser)
  }

}
