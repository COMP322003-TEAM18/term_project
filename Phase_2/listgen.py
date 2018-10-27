from item import *
import random

def read_item():
    f = open("item.txt", encoding='UTF8')
    
    item_list = []

    line = f.readline()
    while line:
        sep = '\t'
        att_list = line.strip().split(sep)
        new_item = item(att_list[0], att_list[1], att_list[2], att_list[3], att_list[4], att_list[5], att_list[6], att_list[7], att_list[8], att_list[9])
        item_list.append(new_item)
        line = f.readline()

    f.close()

    print(len(item_list), 'item readed from file.')

    return item_list

def generate_list(item_list, type):
    result_list = []

    # 도, 소매인 경우: 2~10종류 사이로 단위*20~30개
    if type == '도매업' or type == '소매업':
        items = random.sample(range(0, len(item_list)), random.randint(2, 11))
        items.sort()
        for it in items:
            temp_tuple = (item_list[it], item_list[it].quantity * random.randint(20, 31))
            result_list.append(temp_tuple)
    # 기타인 경우: 1~5종류 사이로 단위*1~3개
    else:
        items = random.sample(range(0, len(item_list)), random.randint(1, 6))
        items.sort()
        for it in items:
            temp_tuple = (item_list[it], item_list[it].quantity * random.randint(1, 4))
            result_list.append(temp_tuple)

    # print(len(result_list), 'items included in order_list.')

    return result_list

if __name__ == '__main__':
    item_list = read_item()
    order_list = generate_list(item_list, '소매업')
    
    for litem in order_list:
        print(litem[0], litem[1], sep=', ')
