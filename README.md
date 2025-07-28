#Employee Check-In

```
Overview:
	This is the workflow or the approach for a Employee Check-In system (a Command Line Interface (CLI)) built using Kotlin.
We aim to check-in the employee using their emp_id and to validate their check-in.

Data Classes:
	-EmployeeDetails
		-emp_id:int
		-firstName:String 
		-lastName:String
		-role:String
		-dept:String
		-reportingTo:int
	-AttendanceEntry
		-emp_id:int
		-checkInDate:String
		-checkInTime:String

Funtionalities:
	-addEmployee():
		fetches the data of the employee such as the first name , last name , role , dept , reporting to emp_id and 
	for ach employee a unique id is assigned
	
	-isEmployeeExist(emp_id)
		the emp_id checks in the in the list containing all the EmployeeDetails and return whether the id is present or not

	-isCheckedIn(emp_id)
		this function checks whether the employee with emp_id has already checked in for the day by checking in the list containing all the AttendanceEntry 
	 whether the employee with emp_id , has an entry present in todaysDate

	-isCheckedIn(emp_id,checkInDate)
		this function checks whether the employee with emp_id has already checked in for the day by checking in the list containing all the AttendanceEntry 
	 whether the employee with emp_id , has an entry present in checkInDate

	-checkInEmployee(emp_id){
		this functions uses the above two functions to validate the employee.If it passes all the validation (i.e emp_id must be present in EmployeeDetails list 
	and emp_id with todayDate must not be in AttendanceEntry list) then add the emp_id along with the checkInDate and checkInTime generated realtime by the os in 
	the AttendanceEntry

	-checkedInt(emp_id,checkInDate,checkInTime)
		this functions uses the above two functions to validate the employee.If it passes all the validation (i.e emp_id must be present in EmployeeDetails list 
	and emp_id with checkInDate must not be in AttendanceEntry list) then add the emp_id along with the checkInDate and checkInTime in 
	the AttendanceEntry
	
```
