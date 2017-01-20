package queries

import java.time.LocalDateTime

import datamodel.DataModel.Task
import datamodel.{Priority, DataModel}
import org.scalatest.{BeforeAndAfterAll, FunSpec, Matchers}
import queries._
import slick.driver.H2Driver.api._

import scala.concurrent._
import scala.concurrent.duration._


class QuerieSpec  extends FunSpec with Matchers  with BeforeAndAfterAll {

  var db: Database = _
  var t1: Task = _
  var t2: Task = _
  var t3: Task = _
  var t4: Task = _
  var t5: Task = _
  var t6: Task = _
  var t7: Task = _

  override protected def beforeAll(): Unit = {
    db = Database.forConfig("taskydb")
    Await.result(db.run(DataModel.createTaskTableAction), 2 seconds)
    t1 = Task(title = "Write part 1 blog on Slick", dueBy = LocalDateTime.now().minusDays(7), tags = Set("blogging", "scala", "slick"), priority = Priority.HIGH)
    t2 = Task(title = "Give a Java 8 training", dueBy = LocalDateTime.now().minusDays(3), tags = Set("java", "training", "travel"), priority = Priority.LOW)
    t3 = Task(title = "Write part 2 blog on Slick queries", dueBy = LocalDateTime.now(), tags = Set("blogging", "scala", "slick"), priority = Priority.HIGH)
    t4 = Task(title = "Read Good to Great book", dueBy = LocalDateTime.now().plusDays(15), tags = Set("reading", "books", "startup"), priority = Priority.MEDIUM)
    t5 = Task(title = "Read Programming Scala book", dueBy = LocalDateTime.now().plusDays(30), tags = Set("reading", "books", "scala"), priority = Priority.HIGH)
    t6 = Task(title = "Go to Goa for holiday", dueBy = LocalDateTime.now().plusDays(60), tags = Set("travel"), priority = Priority.LOW)
    t7 = Task(title = "Build my dream application using Play framework and Slick", dueBy = LocalDateTime.now().plusMonths(3), tags = Set("application", "play", "startup"), priority = Priority.HIGH)
    val tasks = Seq(t1, t2, t3, t4, t5, t6, t7)
    performAction(DataModel.insertTaskAction(tasks: _*))
  }

  private def performAction[T](action: DBIO[T]): T = {
    Await.result(db.run(action), 2 seconds)
  }

  describe("Task Data Model Query Spec") {

    it("should select all the tasks stored in the database") {
      val tasks = performAction(selectAllTasksQuery.result)
      tasks should have length 7
      tasks.head should have(
        'title (t1.title),
        'description (t1.description),
        'createdAt (t1.createdAt),
        'dueBy (t1.dueBy),
        'tags (t1.tags)
      )
    }

    it("should select all task titles") {
      val taskTitles = performAction(selectAllTaskTitleQuery.result)
      taskTitles should have length 7
      taskTitles should be(List(t1.title, t2.title, t3.title, t4.title, t5.title, t6.title, t7.title))
    }

    it("should select all the high priority task titles"){
      val highPriorityTasks = performAction(selectHighPriorityTasksQuery.result)
      highPriorityTasks should have length 4
      highPriorityTasks should be(List(t1.title, t3.title, t5.title, t7.title))
    }

    it("should sort tasks in descending order of due date") {
      val tasks = performAction(selectTasksSortedByDueDateDescQuery.result)
      tasks.head should have(
        'title (t7.title),
        'description (t7.description),
        'createdAt (t7.createdAt),
        'dueBy (t7.dueBy),
        'tags (t7.tags)
      )
    }
  }

}
