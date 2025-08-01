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



class Employee( val id: String,
                val firstName: String,
                val lastName: String,
                val role: Role,
                val dept: Department,
                val reportingTo: String?
) {
    override fun toString(): String {
        return "Employee Id:$id  Name:$firstName $lastName role:$role dept:$dept reporting to : ${reportingTo ?: "N/A"}"
    }

    val errors = mutableListOf<String>()

    fun isValidate(existingIds:Set<String>): Boolean{
        if(firstName.isBlank()){
            errors.add("First Name cannot be blank")
        }
        if(lastName.isBlank()){
            errors.add("Last Name cannot be blank")
        }
        if(id in existingIds){
            errors.add("Employee already with Id ${id} already exists")
        }
        return errors.isEmpty()
    }

    fun showError(){
        println("Error : "+ errors)
    }
}
class EmployeeList : ArrayList<Employee>() {

    private var nextEmpId = 1

    fun generateEmployeeId(): String {
        val id = "PQ%03d".format(nextEmpId)
        nextEmpId++
        return id
    }

    fun isEmployeeExist(empId: String): Boolean {
        return this.any { it.id == empId }
    }

    override fun add(employee: Employee) : Boolean{
        val existingIds = this.mapTo(HashSet()) { it.id }
        if(!employee.isValidate(existingIds)){
            return false
        }
        return super.add(employee)
    }

}
class Attendance(
    val empId: String,
    val checkInTime: LocalTime,
    val checkInDate: LocalDate,
    var checkOutTime: LocalTime? = null,
    var workingHours: String? = null
) {

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
    fun isValid(attendanceEntries:List<Attendance>):Boolean{

        if(isFutureDateTime(checkInDate,checkInTime)){
            errors.add("Date or Time cannot be in the future")
            return false
        }
        if(attendanceEntries.any{it.empId==empId && it.checkInDate==checkInDate}){
            errors.add("CheckIn entry for the day already exist")
            return false
        }
        if(attendanceEntries.any{it.empId==empId && it.checkInDate==checkInDate && it.checkOutTime!=null}){
            errors.add("CheckOut entry for the day already exist")
            return false
        }
        return errors.isEmpty()
    }

    fun isValidCheckOut(attendanceEntries: List<Attendance>,checkOutTime:LocalTime):Boolean{
        val entry = attendanceEntries.find { it.empId == empId && it.checkInDate == checkInDate && it.checkOutTime == null }
        if(entry==null){
            errors.add("Cannot checkOut : No checkIn recorded.")
            return false
        }
        if(isFutureDateTime(entry.checkInDate,entry.checkInTime)){
            errors.add("Date or Time cannot be in future")
            return false
        }
        return errors.isEmpty()
    }

    fun showError(){
        println("Entry Failed :"+errors)
    }
    override fun toString(): String {
        return "EmpId: $empId | Date: $checkInDate | In: $checkInTime | Out: ${checkOutTime ?: "--"} | Hours: ${workingHours ?: "--"}"
    }
}
class AttendanceList : ArrayList<Attendance>() {

    override fun add(entry: Attendance): Boolean {
        if(!entry.isValid(this)){
            return false
        }
        return super.add(entry)
    }


    fun checkOutEmployee(
        empId: String,
        checkOutDate: LocalDate = LocalDate.now(),
        checkOutTime: LocalTime = LocalTime.now()
    ): Boolean {

        val entry = find { it.empId == empId && it.checkInDate == checkOutDate && it.checkOutTime == null }
        if (entry != null) {
            val checkInTime = entry.checkInTime
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
            entry.checkOutTime = checkOutTime
            entry.workingHours = workingHoursStr
            return true
        }
        return false
    }
}

class EmployeeManager(val empList: EmployeeList = EmployeeList(), val attList: AttendanceList = AttendanceList()){

    fun addEmployee(
        firstName: String,
        lastName: String,
        role: Role,
        dept: Department,
        reportingTo: String?
    ):Boolean{
        val id=empList.generateEmployeeId()
        if (reportingTo != null && !empList.isEmployeeExist(reportingTo)) {
            println("Reporting manager ID does not exist.")
            return false
        }
        val emp = Employee(id, firstName, lastName, role, dept, reportingTo)
        if(!empList.add(emp)){
            emp.showError()
            return false
        }
        return true
    }

    fun checkIn(empId: String, date: LocalDate = LocalDate.now(), time: LocalTime = LocalTime.now()): Boolean {
        if (!empList.isEmployeeExist(empId)) {
            println("Employee ID $empId does not exist.")
            return false
        }
        val entry = Attendance(empId, time, date)
        if(!attList.add(entry)){
            entry.showError()
            return false
        }
        return true
    }
    fun checkOut(empId: String, date: LocalDate = LocalDate.now(), time: LocalTime = LocalTime.now()): Boolean {
        return attList.checkOutEmployee(empId, date, time)
    }
    fun viewEmployees() {
        empList.forEach {println(it)}
    }
    fun viewAttendance() {
        attList.forEach { println(it) }
    }
}



class TakeAttendance {
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
}
fun main() {
    val inp=TakeAttendance()
    val empList=EmployeeList()
    val attList=AttendanceList()

    val manager=EmployeeManager(empList,attList)

    manager.addEmployee("Alice", "Smith", Role.DEV, Department.IT, "PQ003")
    manager.addEmployee("Bob", "Johnson", Role.QA, Department.IT, "PQ001")
    manager.addEmployee("Charlie", "Brown", Role.HR, Department.ADMIN, null)
    manager.addEmployee("Diana", "Prince", Role.MANAGER, Department.IT, "PQ003")
    manager.addEmployee("Eve", "Adams", Role.SUPPORT, Department.CUSTOMER_SERVICE, "PQ004")

    var flag = true
    while (flag) {
        println("CheckIn Option:\n1.addEmployee\n2.checkIn with EmpId\n3.checkIn with EmpId,CustomDate and time(Date format:dd-MM-yyy  Time format:HH:mm)\n4.checkOut\n5.checkOut with custom Date and time\n6.view all employees\n7.view all Attendance entries\n8.exit")
        println("Enter your option:")
        val op = readln().toInt()
        when (op) {
            1 -> {
                    println("Enter first name :")
                val firstName=readln()
                println("Enter Last name:")
                val lastName=readln()
                val role=inp.getRole()
                val dept=inp.getDept()
                println("Enter reporting manager Id if present :(else leave blank) ")
                val reportingTo=readln().takeIf(){it.isNotBlank()}
                if(manager.addEmployee(firstName,lastName,role,dept,reportingTo)){
                    println("Employee added successfully .")
                }
            }

            2 -> {
               println("Emter the empId:")
                val empId=readln()
                if(manager.checkIn(empId)){
                    println("checkedIn succesfully")
                }
            }

            3 -> {
                println("Enter the employee Id:")
                val empId=readln()
                println("Enter the date:")
                val date=inp.getDate()
                println("Enter the time:")
                val time=inp.getTime()

                if(manager.checkIn(empId,date,time)){
                    println("checkedIn succesfully")
                }
            }


            4 -> {
                println("Enter the employee Id:")
                val empId=readln()
                if(manager.checkOut(empId)){
                    println("Checked Out succesfully")
                }
            }

            5 -> {
                println("Enter the employee Id:")
                val empId=readln()
                val date=inp.getDate()
                val time=inp.getTime()
                if(manager.checkOut(empId,date,time)){
                    println("Checkedout successfully ")
                }
            }

            6 -> {
                manager.viewEmployees()
            }

            7 -> {
                manager.viewAttendance()
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


