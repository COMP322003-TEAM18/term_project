import datetime
import calendar
import random
from customer import *
from listgen import *

def read_customer():
    f = open("customer.txt", encoding='UTF8')
    
    customer_list = []

    line = f.readline()
    while line:
        sep = '\t'
        att_list = line.strip().split(sep)
        new_customer = customer(att_list[0], att_list[1], att_list[2], att_list[3], att_list[4], att_list[5], att_list[6], att_list[7], att_list[8], att_list[9], att_list[10])
        customer_list.append(new_customer)
        line = f.readline()

    f.close()

    print(len(customer_list), 'customer readed from file.')

    return customer_list

def generate():
    customer_list = read_customer()
    item_list = read_item()
    ordered_list = []
    start_month = datetime.datetime(2015, 6, 1)
    end_month = datetime.datetime(2018, 9, 30)
    shippingcompany = [("서울인천경기강원", "SH00000001"), ("부산대구울산경북경남", "SH00000002"), ("광주전북전남대전세종충북충남제주", "SH00000003")];    
    '''
    a = ["SH00000001"]
    for i in range(1, 120):
        a.append("SH00000001")
    for i in range(0,140):
        a.append("SH00000002")
    for i in range(0,140):
        a.append("SH00000003")
    random.shuffle(a)
    '''
    count = 0
    temp_customer = [0,0,0,0,0]
    for i in range(0, 30):
        temp = random.randint(7, 9)
        temp_customer.append(temp)
        count += temp
    print("count : ", count)

    count = 400 - count - 35
    count2 = 0
    for i in range(0,35):
        if i < 10:
            temp_int = random.randint(0, 6)
        else:
            temp_int = random.randint(8, 20)
        if count - temp_int > 0:
            temp_customer.append(temp_int+1)
            count -= temp_int
            count2 += temp_int
        else:
            temp_customer.append(count+1)
            count2 += count
            count = 0
    print(count2+35)
    random.shuffle(temp_customer)
    
    c_id = []
    for i in range(0, 70):
        if i in range(0, 30):
            temp_cid = "C2000000%02d" % (i+1)
        elif i in range(30, 50):
            temp_cid = "C1000000%02d" % (i-29)
        elif i in range(50, 70):
            temp_cid = "C0000000%02d" % (i-49)
        for j in range(0, temp_customer[i]):
            c_id.append(temp_cid)
    random.shuffle(c_id)
    
    cnt = 0
    for year in range(start_month.year, end_month.year + 1):
        if year == start_month.year: 
            month = start_month.month
        else:
            month = 1

        while month <= 12:
            if year == end_month.year and month > end_month.month:
                break
            daylist = random.sample(range(1, calendar.monthrange(year, month)[1] + 1), 10)
            daylist.sort()
            for o_day in daylist:
                    cnt += 1
                    so_id = "SO%08d" % cnt
                    temp = customer("0")
                    for i in customer_list:
                        temp=i
                        if c_id[cnt-1] == i.C_id :
                            order_list = generate_list(item_list, i.Type)
                            for j in order_list:
                                ordered_list.append("INSERT INTO ORDER_LIST VALUES ('%s', '%s', %d);" % (so_id,j[0].i_code,j[1]))
                            break

                    if temp.Fname == "NULL":
                        Rname = "NULL"
                    else:
                        Rname = "'{0}{1}'".format(temp.Lname, temp.Fname)
                    if shippingcompany[0][0].find(temp.Address[0:2]) != -1:
                        sh_id = shippingcompany[0][1]
                    elif shippingcompany[1][0].find(temp.Address[0:2]) != -1:
                        sh_id = shippingcompany[1][1]
                    else:
                        sh_id = shippingcompany[2][1]
                    
                    hour = random.randint(0, 23)
                    minute = random.randint(0, 59)
                    second = random.randint(0, 59)

                    print("INSERT INTO SHIPPINGORDER VALUES ('%s', '%s', %s, '%s', %s, '%s', '%s');" % (so_id, temp.Address, Rname, temp.Tel, "STR_TO_DATE('%4d-%02d-%02d %02d:%02d:%02d', '%%Y-%%m-%%d %%T')" % (year, month, o_day, hour, minute, second), c_id[cnt-1], sh_id)) # 시작월~종료월까지 각 월당 5일 랜덤으로 뽑음       
            month += 1

    print("\n\n")
    for i in ordered_list:
        print(i)

generate()
