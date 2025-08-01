import java.time.LocalDate
import java.time.LocalTime
import java.time.Duration
import java.time.format.DateTimeFormatter


enum class Department {
    IT,
    ADMIN,
    CUSTOMER_SERVICE;
}

enum class Role {
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
        return "Employee Id:$id  Name:$firstName $lastName role:$role dept:$dept reporting to : ${reportingTo ?: "N/A"}"
    }
}

data class AttendanceEntry(
    val empId: String,
    val checkInTime: LocalTime,
    val checkInDate: LocalDate,
    var checkOutTime: LocalTime? = null,
    var workingHours: String? = null
)
class EmployeeList : ArrayList<EmployeeDetails>() {
    override fun add(employee: EmployeeDetails): Boolean {
        val emp=Employee()
        if (!emp.isValidate(employee)) {
            return false
        }
        return super.add(employee)
    }
}

class Employee {
    private var employees: EmployeeList = EmployeeList()
    val errors=mutableListOf<String>()
    private var nextEmpId = 1
    fun generateEmployeeId(): String {

        val id = "PQ%03d".format(nextEmpId)
        nextEmpId++
        return id
    }

    fun isValidate(employee:EmployeeDetails): Boolean{
        if(employee.firstName.isBlank()){
            errors.add("First Name cannot be blank")
        }
        if(employee.lastName.isBlank()){
            errors.add("Last Name cannot be blank")
        }
        if(this.employees.any{ it.id==employee.id }){
            errors.add("Employee already with Id ${employee.id} already exists")
        }
        return errors.isEmpty()
    }
    fun isEmployeeExist(empId: String): Boolean {
        return employees.any { it.id == empId }
    }

    fun addEmployee(employee: EmployeeDetails) : Boolean{
        if(!isValidate(employee)){
            return false
        }
        return employees.add(employee)
    }

    fun showError(){
        println("Error : "+ errors)
    }

    fun viewAllEmployees() {
        if (employees.isEmpty()) {
            println("No employees added")
            return
        }
        employees.forEach {
            println(it)
        }
    }
}

class AttendanceList : ArrayList<AttendanceEntry>() {

    fun isFutureDateTime(date: LocalDate, time: LocalTime): Boolean {
        val currDate = LocalDate.now()
        val currTime = LocalTime.now()
        if (date.isAfter(currDate)) {
            return true
        } else if (date == currDate && time.isAfter(currTime)) {
            return true
        }
        return false
    }

    override fun add(entry: AttendanceEntry): Boolean {

        if (isFutureDateTime(entry.checkInDate, entry.checkInTime)) {
            return false
        }
        if (this.any { it.empId == entry.empId && it.checkInDate == entry.checkInDate && it.checkOutTime == null }) {
            return false
        }
        if (this.any { it.empId == entry.empId && it.checkInDate == entry.checkInDate && it.checkOutTime != null }) {
            return false
        }
        return super.add(entry)
    }
}

class Attendance {
    private var attendanceEntries: AttendanceList = AttendanceList()
    val errors=mutableListOf<String>()
    fun isFutureDateTime(date: LocalDate, time: LocalTime): Boolean {
        val currDate = LocalDate.now()
        val currTime = LocalTime.now()
        if (date.isAfter(currDate)) {
            return true
        } else if (date == currDate && time.isAfter(currTime)) {
            return true
        }
        return false
    }
    fun isValid(entry:AttendanceEntry):Boolean{

        if(isFutureDateTime(entry.checkInDate,entry.checkInTime)){
            errors.add("Date or Time cannot be in the future")
            return false
        }
        if(this.attendanceEntries.any{it.empId==entry.empId && it.checkInDate==entry.checkInDate && it.checkOutTime==null}){
            errors.add("CheckIn entry for the day already exist")
            return false
        }
        if(this.attendanceEntries.any{it.empId==entry.empId && it.checkInDate==entry.checkInDate && it.checkOutTime!=null}){
            errors.add("CheckOut entry for the day already exist")
            return false
        }
        return errors.isEmpty()
    }
    fun checkInEmployee(
        empId: String,
        checkInDate: LocalDate = LocalDate.now(),
        checkInTime: LocalTime = LocalTime.now()
    ): Boolean {

        val entry = AttendanceEntry(empId, checkInTime, checkInDate)
        if(!isValid(entry)){
            return false
        }
        return attendanceEntries.add(entry)
    }

    fun checkOutEmployee(
        empId: String,
        checkOutDate: LocalDate = LocalDate.now(),
        checkOutTime: LocalTime = LocalTime.now()
    ): Boolean {

        val cIn = attendanceEntries.find { it.empId == empId && it.checkInDate == checkOutDate && it.checkOutTime == null }
        if (cIn != null) {
            val checkInTime = cIn.checkInTime
            val workingHour = Duration.between(checkInTime, checkOutTime)
            val workingHoursStr=workingHour?.let { duration ->
                val hours=duration.toHours()
                val minutes=duration.toMinutes()%60
                if(hours>0 && minutes>0){
                    "$hours H $minutes m"
                }
                else if(hours>0){
                    "$hours H"
                }
                else if(minutes>0){
                    "$minutes m"
                }
                else {
                    "CheckIn and CheckOut are at the same Time.No Working Hours"
                }
            }
            cIn.checkOutTime = checkOutTime
            cIn.workingHours = workingHoursStr
            return true
        }
        return false
    }
    fun showError(){
        println("Entry Failed :"+errors)
    }
    fun viewAllEntries() {
        if (attendanceEntries.isEmpty()) {
            println("No Attendance entry recorded")
            return
        } else {
            val comparator=compareBy<AttendanceEntry> { it.checkInDate }.thenBy{it.checkInTime}
            attendanceEntries.sortWith(comparator)
            attendanceEntries.forEach {
                println(it)
            }
        }
    }
}



class TakeAttendance {
    val emp = Employee()
    val attendance = Attendance()
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

    fun getRole(): Role {
        while (true) {
            println("Enter Role ${Role.entries}:")
            val role = readln().uppercase()
            try {
                return Role.valueOf(role)
            } catch (e: IllegalArgumentException) {
                println("Invalid Role. Please choose from the list")
            }
        }
    }

    fun getDept(): Department {
        while (true) {

            println("Enter your department ${Department.entries} :")
            val dept = readln().uppercase()
            try {
                return Department.valueOf(dept)
            } catch (e: IllegalArgumentException) {
                println("Invalid Department. Please choose from the list.")
            }
        }
    }


    fun handleAddEmployee() {
        val empId = emp.generateEmployeeId()
        println("Enter employee first name:")
        val firstName = readln()
        println("Enter employee last name:")
        val lastName = readln()
        val role = getRole()
        val dept = getDept()
        println("Enter reporting to ID (or leave blank if none):")
        var reportingTo: String? = null
        val reportingToInput = readln().trim()
        if (reportingToInput.isNotBlank()) {
            if (!emp.isEmployeeExist(reportingToInput)) {
                println("Error.Reporting  Id does not exist in employee List.")
                return
            } else {
                reportingTo = reportingToInput
            }

        }
        val newEmployee = EmployeeDetails(empId, firstName, lastName, role, dept, reportingTo)

        if(!emp.addEmployee(newEmployee)){
            emp.showError()
        }
        else{
            println("Employee added . Employee Id : ${newEmployee.id}")
        }

    }
    fun handleCheckIn(){
        println("Enter empId")
        val empId = readln()
        if (!emp.isEmployeeExist(empId)) {
            println("CheckIn failed:Employee with id $empId does not exist")
        } else {
            val isCheckedIn = attendance.checkInEmployee(empId)
            if (isCheckedIn) {
                println("You have been CheckedIn Successfully")
            }
        }
    }
    fun handleCheckInWithDateTime() {
        println("Enter emp Id")
        val empId = readln()
        if (!emp.isEmployeeExist(empId)) {
            println("Check In failed:Employee with id $empId does not exist")
        } else {
            println("Enter Date (Format dd-MM-yyyy):")
            val date = getDate()

            println("Enter Time (Format HH:mm):")
            val time = getTime()


            val isCheckedIn = attendance.checkInEmployee(empId, date, time)
            if (isCheckedIn) {
                println("Checked In successfully")
            }
        }
    }
    fun handleCheckOut(){
        println("Enter employee Id")
        val empId = readln()
        if (!emp.isEmployeeExist(empId)) {
            println("Check Out failed:Employee with id $empId does not exist")
        } else {
            if ( attendance.checkOutEmployee(empId)) {
                println("You have been checkedOut successfully.")
            } else {
                println("CheckOut failed: Cannot CheckOut without checkIn")
            }
        }
    }
    fun handleCheckOutWithDateTime(){
        println("Enter employee Id")
        val empId = readln()
        if (!emp.isEmployeeExist(empId)) {
            println("Check Out failed:Employee with id $empId does not exist")
        } else {
            println("Enter the Date in the format 'dd-MM-yyyy'")
            val date = getDate()
            println("Enter the Time in the format 'HH:mm")
            val time = getTime()
            if (attendance.checkOutEmployee(empId, date, time)) {
                println("You have been checkedOut successfully.")
            } else {
                println("CheckOut failed: Cannot CheckOut without checkIn")
            }
        }
    }
}
fun main() {
    val inp=TakeAttendance()
    val emp=Employee()
    val attendance=Attendance()
    emp.addEmployee(EmployeeDetails(emp.generateEmployeeId(), "Alice", "Smith", Role.DEV, Department.IT, "PQ003"))
    emp.addEmployee(EmployeeDetails(emp.generateEmployeeId(), "Bob", "Johnson", Role.QA, Department.IT, "PQ001"))
    emp.addEmployee(EmployeeDetails(emp.generateEmployeeId(), "Charlie", "Brown", Role.HR, Department.ADMIN, null))
    emp.addEmployee(EmployeeDetails(emp.generateEmployeeId(), "Diana", "Prince", Role.MANAGER, Department.IT, "PQ003"))
    emp.addEmployee(
        EmployeeDetails(
            emp.generateEmployeeId(),
            "Eve",
            "Adams",
            Role.SUPPORT,
            Department.CUSTOMER_SERVICE,
            "PQ004"
        )
    )
    var flag = true
    while (flag) {
        println("CheckIn Option:\n1.addEmployee\n2.checkIn with EmpId\n3.checkIn with EmpId,CustomDate and time(Date format:dd-MM-yyy  Time format:HH:mm)\n4.checkOut\n5.checkOut with custom Date and time\n6.view all employees\n7.view all Attendance entries\n8.exit")
        println("Enter your option:")
        val op = readln().toInt()
        when (op) {
            1 -> {
                inp.handleAddEmployee()
            }

            2 -> {
                inp.handleCheckIn()
            }

            3 -> {
               inp.handleCheckInWithDateTime()
            }


            4 -> {
                inp.handleCheckOut()
            }

            5 -> {
                inp.handleCheckOutWithDateTime()
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


