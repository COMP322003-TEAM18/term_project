class item:
    def __init__(self, i_code, name, spec, quantity, unit, stock, price, min_quantity, sc_id, b_id):
        self.i_code = i_code
        self.name = name
        self.spec = spec
        self.quantity = int(quantity)
        self.unit = unit
        self.stock = int(stock)
        self.price = int(price)
        self.min_quantity = int(min_quantity)
        self.sc_id = sc_id
        self.b_id = b_id

    def __str__(self):
        return '<' + self.i_code + ', ' + self.name + ', ' + self.spec + ', ' + str(self.quantity) + ', ' + self.unit + ', ' + str(self.stock) + ', ' + str(self.price) + ', ' + str(self.min_quantity) + ', ' + self.sc_id + ', ' + self.b_id + '>'
