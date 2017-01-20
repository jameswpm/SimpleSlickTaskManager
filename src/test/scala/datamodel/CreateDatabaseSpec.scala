package datamodel

import java.time.LocalDateTime

import datamodel.DataModel.Task
import org.scalatest.{FunSpec, Matchers}
import slick.driver.H2Driver.api._

import scala.concurrent._
import scala.concurrent.duration._


class CreateDatabaseSpec extends FunSpec with Matchers {
    describe ("Datamodel Spec") {
      it("should create database") {
        val db = Database.forConfig("taskydb")
        val result = Await.result(db.run(DataModel.createTaskTableAction), 2 seconds)
        println(result)
      }

      it("should insert single task into database") {
        val db = Database.forConfig("taskydb")
        val result = Await.result(db.run(DataModel.insertTaskAction(Task(title="Learn Slick", dueBy = LocalDateTime.now().plusDays(1)))), 2 seconds)
        result should be(Some(1))
      }

      it("should list all tasks in the database") {
        val db = Database.forConfig("taskydb")
        val tasks = Seq(
          Task(title = "Learn Slick again", dueBy = LocalDateTime.now().plusDays(1)),
          Task(title = "Write blog on Slick", dueBy = LocalDateTime.now().plusDays(2)),
          Task(title = "Build a simple application using Slick", dueBy = LocalDateTime.now().plusDays(3))
        )
        Await.result(db.run(DataModel.insertTaskAction(tasks: _*)), 2 seconds)
        val result = Await.result(db.run(DataModel.listAllTasksAction), 2 seconds)
        result should have length 4// one is for the previous test that runs at same time
      }
    }
}
