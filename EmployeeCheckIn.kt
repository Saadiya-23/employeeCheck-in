
import java.time.LocalDate
import java.time.LocalTime
import java.time.Duration
import java.time.format.DateTimeFormatter


enum class Department{
    IT,
    ADMIN,
    CUSTOMER_SERVICE;
}
enum class Role{
    DEV,
    QA,
    HR,
    MANAGER,
    SUPPORT;
}

data class EmployeeDetails(
    val id: String,
    val firstName: String,
    val lastName: String,
    val role: Role,
    val dept: Department,
    val reportingTo: String?
) {
    override fun toString(): String {
        return "empId=${id}  firstName=${firstName} lastName=${lastName} role=${role} dept=${dept} reporting to = ${reportingTo ?: "N/A"}"
    }
}

data class AttendanceEntry(
    val empId:String,
    val checkInTime: LocalTime,
    val checkInDate: LocalDate,
    var checkOutTime: LocalTime?=null,
    var workingHours: Duration?=null
){
    override fun toString(): String {

        val checkOutStr=checkOutTime?.format(DateTimeFormatter.ofPattern("HH:mm"))?:"Haven't checkOut Yet"
        val workingHourStr=workingHours?.let{ duration ->
            val hours=duration.toHours()
            val minutes=duration.toMinutes()%60
            if(hours>0 && minutes>0){
                "$hours H $minutes M"
            }
            else if(hours>0){
                "$hours H"
            }
            else if(minutes>0){
                "$minutes m "
            }
            else {
                "CheckIn and CheckOut has the same Time "
            }
        } ?: "Cannot calculate working hours without CheckOut"

        return "Employee Id : $empId Date(CheckIn and CheckOut):${checkInDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))}\nCheckIn Time:${checkInTime.format(
            DateTimeFormatter.ofPattern("HH:mm"))} CheckOut Time:$checkOutStr \n working Hour:$workingHourStr "
    }
}

class Input {
    fun getId(): String {
        val id = readln()
        return id
    }

    fun getDate(): LocalDate {
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        while (true) {
            val dateStr = readln()
            try {
                return LocalDate.parse(dateStr, dateFormatter)
            } catch (e: Exception) {
                println("Invalid format. Please use dd-MM-yyyy for date ")
            }
        }
    }


    fun getTime(): LocalTime {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        while (true) {
            val timeStr = readln()
            try {
                return LocalTime.parse(timeStr, timeFormatter)
            } catch (e: Exception) {
                println("Invalid format : Please use HH:mm format ")
            }
        }
    }

    fun getRole():Role{
        while(true) {
            println("Enter Role (${Role.entries.toString()}):")
            val role = readln().uppercase()
            try {
                return Role.valueOf(role)
            } catch (e: IllegalArgumentException) {
                println("Invalid Role. Please choose from the list")
            }
        }
    }

    fun getDept():Department{
        while(true) {

            println("Enter your department(${Department.entries.toString()}) :")
            val dept = readln().uppercase()
            try {
                return Department.valueOf(dept)
            } catch (e: IllegalArgumentException) {
                println("Invalid Department. Please choose from the list.")
            }
        }
    }


}
class EmployeeList : ArrayList<EmployeeDetails>(){
    override fun add(employee : EmployeeDetails):Boolean{
        if(employee.firstName.isBlank() || employee.lastName.isBlank()){
            println("First name or Last name cannot be blank.Employee not added")
            return false
        }
        if(this.any{it.id==employee.id}){
            println("Employee with Id ${employee.id} already Exist.  ")
            return false
        }
        println("Employee with Name:${employee.firstName} ${employee.lastName} has been added successfully\nEmployee Id:${employee.id}")
        return super.add(employee)
    }
}
class Employee {
    private var employees : EmployeeList  =EmployeeList()
    private var nextEmpId=1
    fun generateEmployeeId():String{

        val id="PQ%03d".format(nextEmpId)
        nextEmpId++
        return id

    }
    fun isEmployeeExist(empId: String): Boolean {
        return employees.any { it.id == empId }
    }
    fun addEmployee(employee: EmployeeDetails) {
        employees.add(employee)
    }
    fun viewAllEmployees(){
        if (employees.isEmpty()) {
            println("No employees added")
            return
        }
        employees.forEach {
          println(it)
        }
    }
}
class AttendanceList : ArrayList<AttendanceEntry>(){

    fun isFutureDateTime(date: LocalDate,time:LocalTime): Boolean {
        val currDate = LocalDate.now()
        val currTime=LocalTime.now()
        if (date.isAfter(currDate)) {
            return true
        }
        else if(date==currDate &&  time.isAfter(currTime)){
            return true
        }
        return false
    }

    override fun add(entry:AttendanceEntry):Boolean{

        if(isFutureDateTime(entry.checkInDate,entry.checkInTime)){
            println("Entry not added : Cannot use future date or time\nUse current or past dates")
            return false
        }
        if(this.any{it.empId==entry.empId && it.checkInDate==entry.checkInDate && it.checkOutTime==null}){
            println("Entry not added : You have already checkedIn for the day!")
            return false
        }
        if(this.any{it.empId==entry.empId && it.checkInDate==entry.checkInDate && it.checkOutTime!=null}){
            println("Entry not added : You have already checkedOut for the day!")
            return false
        }
        println("Entry Added : ")
        return super.add(entry)
    }
}
class Attendance {
    private var attendanceEntries:AttendanceList=AttendanceList()
    fun isFutureDateTime(date: LocalDate,time:LocalTime): Boolean {
        val currDate = LocalDate.now()
        val currTime=LocalTime.now()
        if (date.isAfter(currDate)) {
            return true
        }
        else if(date==currDate &&  time.isAfter(currTime)){
            return true
        }
        return false
    }

    fun isCheckedIn(empId: String, checkInDate: LocalDate=LocalDate.now()): Boolean {
        return attendanceEntries.any { it.empId == empId && it.checkInDate == checkInDate && it.checkOutTime==null}
    }

    fun checkInEmployee(empId: String, checkInDate: LocalDate= LocalDate.now(), checkInTime: LocalTime=LocalTime.now()):Boolean {
        val entry = AttendanceEntry(empId, checkInTime, checkInDate)
        attendanceEntries.add(entry)
        return true
    }

    fun isCheckedOut(empId:String,checkOutDate:LocalDate= LocalDate.now()):Boolean {
        return attendanceEntries.any{it.empId==empId && it.checkInDate==checkOutDate && it.checkOutTime!=null}
    }

    fun checkOutEmployee(empId: String, checkOutDate: LocalDate= LocalDate.now(), checkOutTime: LocalTime=LocalTime.now()):Boolean {
        val cIn = attendanceEntries.find { it.empId == empId && it.checkInDate == checkOutDate && it.checkOutTime==null}
        if (cIn != null) {
            val checkInTime = cIn.checkInTime
            val workingHour = Duration.between(checkInTime, checkOutTime)
            cIn.checkOutTime=checkOutTime
            cIn.workingHours=workingHour
            return true
        }
        return false
    }
    fun viewAllEntries(){
        if (attendanceEntries.isEmpty()) {
            println("No Attendance entry recorded")
            return
        } else {
            attendanceEntries.forEach {
                println(it)
            }
        }
    }
}
val emp = Employee()
val attendance = Attendance()
val input = Input()
fun handleAddEmployee(){
    val empId=emp.generateEmployeeId()
    println("Enter employee first name:")
    val firstName = readln()
    println("Enter employee last name:")
    val lastName = readln()
    val role = input.getRole()
    val dept = input.getDept()
    println("Enter reporting to ID (or leave blank if none):")
    val reportingTo:String?=null
    val reportingToInput=readln().trim()
    if(reportingToInput.isNotBlank()){
        if(!emp.isEmployeeExist(reportingToInput)){
            println("Error.Reporting  Id does not exist in employee List.")
            return
        }
        else {
            val reportingTo=reportingToInput
        }

    }
    val newEmployee = EmployeeDetails(empId, firstName, lastName, role, dept, reportingTo)
    emp.addEmployee(newEmployee)

}
fun main() {

    emp.addEmployee(EmployeeDetails(emp.generateEmployeeId(), "Alice", "Smith", Role.DEV, Department.IT, "PQ003"))
    emp.addEmployee(EmployeeDetails(emp.generateEmployeeId(), "Bob", "Johnson", Role.QA, Department.IT, "PQ001"))
    emp.addEmployee(EmployeeDetails(emp.generateEmployeeId(), "Charlie", "Brown", Role.HR, Department.ADMIN, null))
    emp.addEmployee(EmployeeDetails(emp.generateEmployeeId(), "Diana", "Prince", Role.MANAGER, Department.IT, "PQ003"))
    emp.addEmployee(EmployeeDetails(emp.generateEmployeeId(), "Eve", "Adams", Role.SUPPORT, Department.CUSTOMER_SERVICE, "PQ004"))
    var flag = true
    while (flag) {
        println("CheckIn Option:\n1.addEmployee\n2.checkIn with EmpId\n3.checkIn with EmpId,CustomDate and time(Date format:dd-MM-yyy  Time format:HH:mm)\n4.checkOut\n5.checkOut with custom Date and time\n6.view all employees\n7.view all Attendance entries\n8.exit")
        println("Enter your option:")
        val op = readln().toInt()
        when (op) {
            1 -> {
                   handleAddEmployee()
            }
            2 -> {
                println("Enter empId")
                val empId = input.getId()
                if(!emp.isEmployeeExist(empId)){
                    println("CheckIn failed:Employee with id $empId does not exist")
                }
                else {
                    if (attendance.isCheckedIn(empId)) {
                        println("check In failed:You have already checked In for the day")
                    } else {
                        val isCheckedIn = attendance.checkInEmployee(empId)
                        if (isCheckedIn) {
                            println("You have been CheckedIn Successfully")
                        }
                    }
                }
            }

            3 -> {
                println("Enter emp Id")
                val empId = input.getId()
                if (!emp.isEmployeeExist(empId)) {
                    println("Check In failed:Employee with id $empId does not exist")
                } else {
                    println("Enter Date (Format dd-MM-yyyy):")
                    val date = input.getDate()

                        println("Enter Time (Format HH:mm):")
                        val time = input.getTime()

                        if (attendance.isFutureDateTime(date,time)) {
                            println("CheckIn failed: You cannot checkIn for the future")
                        } else if (attendance.isCheckedIn(empId, date)) {
                            println("heck In failed:You have already checked In for the day")
                        } else {
                            val isCheckedIn = attendance.checkInEmployee(empId, date, time)
                            if (isCheckedIn) {
                                println("Checked In successfully")
                            }
                        }
                    }
                }

                4 -> {
                    println("Enter employee Id")
                    val empId = input.getId()
                    if(!emp.isEmployeeExist(empId)){
                        println("Check Out failed:Employee with id $empId does not exist")
                    }
                    else {

                        if (!attendance.isCheckedIn(empId)) {
                            println("CheckOut failed :  Cannot checkout without checking in for today")
                        } else if (attendance.isCheckedOut(empId)) {
                            println("CheckOut Failed: You have already checked out for the day")
                        } else {
                            val isCheckedOut = attendance.checkOutEmployee(empId)
                            if (isCheckedOut) {
                                println("You have been checkedOut successfully.")
                            }
                        }
                    }
                }

                5 -> {
                    println("Enter employee Id")
                    val empId = input.getId()
                    if (!emp.isEmployeeExist(empId)) {
                        println("Check Out failed:Employee with id $empId does not exist")
                    }
                    else {
                        println("Enter the Date in the format 'dd-MM-yyyy'")
                        val date = input.getDate()
                        println("Enter the Time in the format 'HH:mm")
                        val time = input.getTime()
                        if (attendance.isFutureDateTime(date,time)) {
                            println("CheckOut failed: You cannot checkout for the future")
                        }
                        else if(!attendance.isCheckedIn(empId,date)){
                            println("CheckOut failed :  Cannot checkout without checking in ")
                        }
                        else if (attendance.isCheckedOut(empId, date)) {
                            println("CheckOut failed:You have already checkedOut for the day ")
                        } else {
                            val isCheckOut = attendance.checkOutEmployee(empId, date, time)
                            if (isCheckOut) {
                                println("You have been checkedOut successfully.")
                            }
                        }
                    }
                }

                6 -> {
                    emp.viewAllEmployees()
                }

                7 -> {
                    attendance.viewAllEntries()
                }

                8 -> {
                    flag = false
                }

                else -> {
                    println("Invalid option. Please enter a valid option ")
                }
            }
        }
    }

