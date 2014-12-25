package fabricator

import com.github.nscala_time.time.Imports._
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.scalatest.testng.TestNGSuite
import org.testng.annotations.{DataProvider, Test}

class CalendarTestSuite extends TestNGSuite with LazyLogging {

  val fabr = new Fabricator
  val util = new UtilityService()
  val calendar = fabr.calendar()

  @Test
  def testDefaultDateGetter() = {
    val date = calendar.date()
    logger.info("Checking default date value " + date)
    assert(date.matches("\\d{2}-\\d{2}-\\d{4}"))
  }

  @DataProvider(name = "dateFormats")
  def dateFormats() = {
    Array(Array("dd:mm:yyyy", "\\d{2}:\\d{2}:\\d{4}"),
      Array("mm:dd:yyyy", "\\d{2}:\\d{2}:\\d{4}"),
      Array("dd:mm:yyyy", "\\d{2}:\\d{2}:\\d{4}"),
      Array("dd:MM:yyyy", "\\d{2}:\\d{2}:\\d{4}"),
      Array("dd:MM:YYYY", "\\d{2}:\\d{2}:\\d{4}"),
      Array("dd/MM/YYYY", "\\d{2}/\\d{2}/\\d{4}"),
      Array("dd/MM/YY", "\\d{2}/\\d{2}/\\d{2}"),
      Array("dd-MM-yyyy", "\\d{2}-\\d{2}-\\d{4}"),
      Array("dd.MM.yyyy", "\\d{2}\\.\\d{2}\\.\\d{4}"),
      Array("dd.M.yyyy", "\\d{2}\\.\\d{1,2}\\.\\d{4}"),
      Array("dd-MM-yyyy HH", "\\d{2}-\\d{2}-\\d{4} \\d{2}"),
      Array("dd-MM-yyyy HH:mm", "\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}"),
      Array("dd-MM-yyyy HH:mm:ss", "\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}"),
      Array("dd-MM-yyyy H:m:s", "\\d{1,2}-\\d{1,2}-\\d{4} \\d{1,2}:\\d{1,2}:\\d{1,2}"),
      Array("dd-MM-yyyy H:m:s a", "\\d{1,2}-\\d{1,2}-\\d{4} \\d{1,2}:\\d{1,2}:\\d{1,2} \\w{2}")
    )
  }

  @Test(dataProvider = "dateFormats")
  def testDateGetterWithDifferentFormats(format: String, regex: String) = {
    val date = calendar.date(format)
    logger.info("Checking date value with " + format + " format :" + date)
    assert(date.matches(regex))
  }


  @Test
  def testAmPm() = {
    val ampm = calendar.ampm()
    logger.info("Testing random amPm value : " + ampm)
    assert(ampm.equals("am") || ampm.equals("pm"))
  }

  @Test
  def testSecond() = {
    val second = calendar.second()
    logger.info("Testing random second value: " + second)
    assert(second.toInt >= 0 && second.toInt <= 60)
  }

  @Test
  def testMinute() = {
    val minute = calendar.minute()
    logger.info("Testing random minute value: " + minute)
    assert(minute.toInt >= 0 && minute.toInt < 60)
  }

  @Test
  def testHour() = {
    val hour24 = calendar.hour(true)
    val hour12 = calendar.hour()
    assert(hour24.toInt >= 0 && hour24.toInt < 24)
    assert(hour12.toInt >= 0 && hour12.toInt < 12)
  }

  @Test
  def testDay() = {
    val year = calendar.year()
    val month = calendar.month()
    val day = calendar.day(year.toInt, month.toInt)
    logger.info("Testing random day value: " + day)
    assert(day.toInt > 0 && day.toInt < 31)
  }

  @Test
  def testMonth() = {
    val monthNumber = calendar.month(true)
    logger.info("Checking random month value numeric: " + monthNumber)
    assert(monthNumber.toInt > 0 && monthNumber.toInt < 12)
    val monthLettered = calendar.month(false)
    val months = util.getArrayFromJson("month")
    assert(months.contains(monthLettered))
  }

  @Test
  def testYear() = {
    val year = calendar.year().toInt
    logger.info("Testing random year value: " + year)
    assert(year > 1970 && year < 2015)
  }

  @DataProvider
  def dateDP() = {
    Array(Array(2014, 02, 30, 00, 00, "28-02-2014 12:00"),
      Array(1000, 02, 30, 00, 00, "28-02-1000 12:00"),
      Array(1980, 01, 50, 12, 30, "31-01-1980 12:30"),
      Array(2250, 02, 30, 00, 00, "28-02-2250 12:00"))
  }

  @Test(dataProvider = "dateDP")
  def testDate(year: Int, month: Int, day: Int, hour: Int, minute: Int, expectedResult: String) = {
    val date = calendar.date(year, month, day, hour, minute)
    logger.info("Testing random date: " + date)
    assert(date.equals(expectedResult))
  }

  @Test
  def testDateObject() = {
    val dateObject = calendar.dateObject()
    assert(dateObject.isInstanceOf[DateTime])
    val year = dateObject.year().get()
    val month = dateObject.monthOfYear().get()
    val day = dateObject.dayOfMonth().get()
    val hour = dateObject.hourOfDay().get()
    val minute = dateObject.minuteOfHour().get()
    val second = dateObject.secondOfMinute().get()
    assert(year >= 1970 && year < 2015)
    assert(month > 0 && month < 12)
    assert(day > 0 && day < 31)
    assert(hour >= 0 && hour < 24)
    assert(minute >= 0 && minute < 60)
    assert(second >= 0 && second < 60)
  }

  @DataProvider
  def dateWithPeriodDP() = {
    Array(Array(0, 0, 0, 0, 0, 0, "dd:MM:yyyy", DateTime.now.toString("dd:MM:yyyy")),
      Array(1, 0, 0, 0, 0, 0, "dd:MM:yyyy", DateTime.now.plusYears(1).toString("dd:MM:yyyy")),
      Array(-1, 0, 0, 0, 0, 0, "dd:MM:yyyy", DateTime.now.minusYears(1).toString("dd:MM:yyyy")),
      Array(0, 1, 0, 0, 0, 0, "dd:MM:yyyy", DateTime.now.plusMonths(1).toString("dd:MM:yyyy")),
      Array(0, -1, 0, 0, 0, 0, "dd:MM:yyyy", DateTime.now.minusMonths(1).toString("dd:MM:yyyy")),
      Array(0, 0, 1, 0, 0, 0, "dd-MM-yyyy", DateTime.now.plusWeeks(1).toString("dd-MM-yyyy")),
      Array(0, 0, -1, 0, 0, 0, "dd:MM:yyyy", DateTime.now.minusWeeks(1).toString("dd:MM:yyyy")),
      Array(0, 0, 0, 1, 0, 0, "dd:MM:yyyy", DateTime.now.plusDays(1).toString("dd:MM:yyyy")),
      Array(0, 0, 0, -1, 0, 0, "dd:MM:yyyy", DateTime.now.minusDays(1).toString("dd:MM:yyyy")),
      Array(0, 0, 0, 0, 1, 0, "dd:MM:yyyy", DateTime.now.plusHours(1).toString("dd:MM:yyyy")),
      Array(0, 0, 0, 0, -1, 0, "dd:MM:yyyy", DateTime.now.minusHours(1).toString("dd:MM:yyyy")),
      Array(0, 0, 0, 0, 0, 1, "dd:MM:yyyy", DateTime.now.plusMinutes(1).toString("dd:MM:yyyy")),
      Array(0, 0, 0, 0, 0, -1, "dd:MM:yyyy", DateTime.now.minusMinutes(1).toString("dd:MM:yyyy")),
      Array(1, 1, 0, 0, 0, 0, "dd:MM:yyyy", DateTime.now.plusYears(1).plusMonths(1).toString("dd:MM:yyyy")),
      Array(-1, -1, 0, 0, 0, 0, "dd:MM:yyyy", DateTime.now.minusYears(1).minusMonths(1).toString("dd:MM:yyyy")),
      Array(3, 3, 3, 0, 0, 0, "dd:MM:yyyy", DateTime.now.plusYears(3).plusMonths(3).plusWeeks(3).toString("dd:MM:yyyy")),
      Array(-1, -1, -1, 0, 0, 0, "dd:MM:yyyy", DateTime.now.minusYears(1).minusMonths(1).minusWeeks(1).toString("dd:MM:yyyy")),
      Array(1, 1, 1, 1, 0, 0, "dd:MM:yyyy", DateTime.now.plusYears(1).plusMonths(1).plusWeeks(1).plusDays(1).toString("dd:MM:yyyy")),
      Array(-5, -5, -5, -5, 0, 0, "dd:MM:yyyy", DateTime.now.minusYears(5).minusMonths(5).minusWeeks(5).minusDays(5).toString("dd:MM:yyyy")),
      Array(1, 1, 1, 1, 1, 0, "dd:MM:yyyy HH", DateTime.now.plusYears(1).plusMonths(1).plusWeeks(1).plusDays(1).plusHours(1).toString("dd:MM:yyyy HH")),
      Array(-1, -1, -1, -1, -1, 0, "dd:MM:yyyy HH", DateTime.now.minusYears(1).minusMonths(1).minusWeeks(1).minusDays(1).minusHours(1).toString("dd:MM:yyyy HH")),
      Array(10, 10, 10, 10, 10, 10, "dd:MM:yyyy HH", DateTime.now.plusYears(10).plusMonths(10).plusWeeks(10).plusDays(10).plusHours(10).plusMinutes(10).toString("dd:MM:yyyy HH")),
      Array(-100, -100, -100, -100, -100, -100, "dd:MM:yyyy HH", DateTime.now.minusYears(100).minusMonths(100).minusWeeks(100).minusDays(100).minusHours(100).minusMinutes(100).toString("dd:MM:yyyy HH"))
    )
  }

  @Test(dataProvider = "dateWithPeriodDP")
  def testDateWithPeriod(year: Int, month: Int, week: Int, day: Int, hour: Int, minute: Int, format: String, expectedDate: String) = {
    val date = calendar.dateWithPeriod(year, month, week, day, hour, minute, format)
    logger.info("Testing random date with dateWithPeriod method: "+date)
    assertResult(expectedDate)(date)
  }

}