package queries

import java.time.{LocalDate, LocalDateTime}

import datamodel.columnDataMappers._
import datamodel.DataModel.{Task, TaskTable, Tasks}
import datamodel.Priority
import datamodel.Priority.Priority
import slick.driver.H2Driver.api._

object queries {
  val selectAllTasksQuery: Query[TaskTable, Task, Seq] = Tasks

  val selectAllTaskTitleQuery: Query[Rep[String], String, Seq] = Tasks.map(taskTable => taskTable.title)

  val selectMultipleColumnsQuery: Query[(Rep[String], Rep[Priority], Rep[LocalDateTime]), (String, Priority, LocalDateTime), Seq] = Tasks.map(t => (t.title, t.priority, t.createdAt))

  val selectHighPriorityTasksQuery: Query[Rep[String], String, Seq] = Tasks.filter(_.priority === Priority.HIGH).map(_.title)

  val selectTasksSortedByDueDateDescQuery = Tasks.sortBy(_.dueBy.desc)

  val selectAllTasksDueToday = Tasks
    .filter(t => t.dueBy > LocalDate.now().atStartOfDay() && t.dueBy < LocalDate.now().atStartOfDay().plusDays(1))
    .map(_.title)

  val selectTasksBetweenTodayAndSameDateNextMonthQuery = Tasks.filter(t => t.dueBy.between(LocalDateTime.now(), LocalDateTime.now().plusMonths(1)))
}
