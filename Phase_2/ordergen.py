import datetime
import calendar
import random

def generate():
    start_month = datetime.datetime(2015, 6, 1)
    end_month = datetime.datetime(2018, 9, 30)

    for year in range(start_month.year, end_month.year + 1):
        if year == start_month.year: 
            month = start_month.month
        else:
            month = 1

        while month <= 12:
            if year == end_month.year and month > end_month.month:
                break
            
            daylist = random.sample(range(1, calendar.monthrange(year, month)[1] + 1), 5)
            daylist.sort()

            print(year, month, daylist) # 시작월~종료월까지 각 월당 5일 랜덤으로 뽑음

            month += 1

generate()
