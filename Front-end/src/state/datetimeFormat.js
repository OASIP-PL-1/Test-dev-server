import { defineStore } from 'pinia'

export const useDatetimeFormat = defineStore('counter', () => {
    const days = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat']
    const months = ['Jan','Feb','Mar','Apr','May','June','July','Aug','Sep','Oct','Nov','Dec']

    const timeUnits = ['AM','PM']

    // Mon 23 May 2022
    const showDate = (givenDate) => {
        const day = days[givenDate.getDay()]
        const date = givenDate.getDate()
        const month = months[givenDate.getMonth()]
        const year = givenDate.getFullYear()
        return day + ' ' + date + ' ' + month + ' ' + year
    }
    
    // Mon 23 May 2022 | 16:30
    const showDateTime = (givenDate) => {
        if(givenDate != 'Invalid Date'){
        const day = days[givenDate.getDay()]
        const date = givenDate.getDate()
        const month = months[givenDate.getMonth()]
        const year = givenDate.getFullYear()
        return day + ' ' + date + ' ' + month + ' ' + year + ' | ' + givenDate.toLocaleTimeString('th-TH').substring(0,5)
        }
    }

    // 23 May 2022 | 16:30 UTC
    const showDateTimeZone = (givenDate) => {
        if(givenDate != 'Invalid Date'){
        const date = givenDate.getDate()
        const month = months[givenDate.getMonth()]
        const year = givenDate.getFullYear()
        return date + ' ' + month + ' ' + year + ' | ' + givenDate.toLocaleTimeString('th-TH').substring(0,5) + ' UTC'
        }
    }

    // -- calculate End time ---
    const addMinutes = (date,duration) => {
        const changeDate = date
        changeDate.setMinutes(changeDate.getMinutes()+duration)
        return changeDate
    }

    // 12:30 AM
    const getEndTime = (givenDate) => {
        if(givenDate != 'Invalid Date'){
            let hour = givenDate.getHours()%12
            if(hour == 0){
                hour = 12
            } else if(hour <= 9){
                hour = '0' + hour
            }
            const minute = givenDate.getMinutes() <= 9 ? '0' + givenDate.getMinutes() : givenDate.getMinutes()
            const timeUnit = timeUnits[Math.floor(givenDate.getHours()/12)]
            return hour + ':' + minute + ' ' + timeUnit
        }
    }

    // 16:30
    const showTime = (givenDate) => { 
        return givenDate.toLocaleTimeString('th-TH').substring(0,5)
    }


    // 2022-05-12T00:00 (get Today for min DateTime input addMode)
    const getTodayDatetime = (currentDate) => {
        const date = currentDate.getDate() <= 9 ? '0'+ currentDate.getDate() : currentDate.getDate()
        const month = (currentDate.getMonth()+1) <= 9 ? '0'+ (currentDate.getMonth()+1) : (currentDate.getMonth()+1)
        const year = currentDate.getFullYear()
        return year +'-'+ month +'-'+ date +'T00:00'
    }

    // 2022-05-12 (get Today for min Date input editMode)
    const getTodayDate = (currentDate) => {
        const date = currentDate.getDate() <= 9 ? '0'+ currentDate.getDate() : currentDate.getDate()
        const month = (currentDate.getMonth()+1) <= 9 ? '0'+ (currentDate.getMonth()+1) : (currentDate.getMonth()+1)
        const year = currentDate.getFullYear()
        return year +'-'+ month +'-'+ date
    }

    return { showDate, showDateTime, showDateTimeZone, addMinutes, getEndTime, showTime, getTodayDatetime, getTodayDate}
})
