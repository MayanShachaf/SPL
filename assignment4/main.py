import os.path
import string
import sys
import sqlite3
import atexit
# from persistence import repo, Hat, Supplier, Order


class Hat(object):
    def __init__(self, id, topping, supplier, quantity):
        self.id = id
        self.topping = topping
        self.supplier = supplier
        self.quantity = quantity


class Supplier(object):
    def __init__(self, id, name):
        self.id = id
        self.name = name


class Order(object):
    def __init__(self, id, location, hat):
        self.id = id
        self.location = location
        self.hat = hat


# Data Access Objects:
# All of these are meant to be singletons
class _Hats:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, hat):
        self._conn.execute("""
               INSERT OR REPLACE INTO hats (id, topping, supplier, quantity) VALUES (?, ?, ?, ?)
           """, [hat.id, hat.topping, hat.supplier, hat.quantity])

    def find_all(self, topping):  # take pizza hat topping and return table with id,supplier,quantity
        c = self._conn.cursor()
        a = c.execute("""
            SELECT id, topping, supplier, quantity FROM hats 
            WHERE topping = ?
            ORDER BY supplier
        """, [topping]).fetchall()

        return [Hat(*row) for row in a]

    def update_quantity(self, new_quantity, id):
        self._conn.execute("""
            UPDATE hats 
            SET quantity = ?
            WHERE id = ?
        """, [new_quantity, id])

    def delete_hat(self, id):
        self._conn.execute("""
            DELETE FROM hats WHERE id = ?
                """, [id])


class _Suppliers:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, supplier):
        self._conn.execute("""
                INSERT OR REPLACE INTO suppliers (id, name) VALUES (?, ?)
        """, [supplier.id, supplier.name])

    def find(self, id):
        c = self._conn.cursor()
        c.execute("""
                SELECT * FROM suppliers 
                WHERE id = ?
            """, [id])

        return Supplier(*c.fetchone())


class _Orders:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, order):
        self._conn.execute("""
                INSERT OR REPLACE INTO orders (id, location, hat) VALUES (?, ?, ?)
        """, [order.id, order.location, order.hat])

    def find(self, id):
        c = self._conn.cursor()
        c.execute("""
                SELECT id,location,hat FROM assignments WHERE hat = ?
            """, [id])

        return Order(*c.fetchone())


# The Repository


class _Repository(object):
    def __init__(self):
        self._conn = sqlite3.connect("database.db")
        self.hats = _Hats(self._conn)
        self.suppliers = _Suppliers(self._conn)
        self.orders = _Orders(self._conn)

    def _close(self):
        self._conn.commit()
        self._conn.close()

    def crate_tables(self):
        try:
            self._conn.executescript("""
                CREATE TABLE hats (
                    id      INT         PRIMARY KEY,
                    topping TEXT        NOT NULL,
                    supplier INT        NOT NULL,
                    quantity INT        NOT NULL,

                    FOREIGN KEY(supplier)  REFERENCES suppliers(id)
                    );

                CREATE TABLE suppliers (
                    id       INT       PRIMARY KEY,
                    name     TEXT      NOT NULL  
                    );

                CREATE TABLE orders (
                    id       INT       PRIMARY KEY,
                    location TEXT      NOT NULL,
                    hat      INT       NOT NULL, 
                    FOREIGN KEY(hat)  REFERENCES hats(id)
                    );
             """)
        except sqlite3.OperationalError:
            # if the tables already exist
            pass


# the repository singleton
repo = _Repository()
atexit.register(repo._close)


def main():
    repo.crate_tables()
    config_file = open(sys.argv[1])  # change to sys.args[0]?
    is_first = 1
    hats_num = 0
    supplier_num = 0
    for line in config_file:
        line = line.rstrip()
        list = line.split(',')
        if len(list) == 2 and is_first == 0:
            # 1, Scrabbles
            print(list[0])
            supplier = Supplier(list[0], list[1])
            repo.suppliers.insert(supplier)
        # first line
        if len(list) == 2 and is_first == 1:
            # hats_num = args[0]
            # supplier_num = args[1]
            is_first = 0
        if len(list) == 4:
            # 1,olives,1,10
            #  last = list[3]
            # last = last[:len(last)-1]
            hat = Hat(list[0], list[1], list[2], list[3])
            repo.hats.insert(hat)
    counter = 1
    orders_file = open(sys.argv[1])  # change to sys.args[1]?
    for line in orders_file:
        line = line.rstrip()
        order_args = line.split(',')
        location = order_args[0]
        topping = order_args[1]
        hats_with_topping = repo.hats.find_all(topping)
        first_hat = 0
        suppliers_num = 0
        for h in hats_with_topping:
            hat_id = h.id
            hat_quantity = h.quantity
            first_supplier = h.supplier
            suppliers_num = 1
            break
        if suppliers_num == 1:
            if hat_quantity > 1:
                repo.hats.update_quantity(hat_quantity - 1, hat_id)
            else:
                repo.hats.delete_hat(hat_id)
            order = Order(counter, location, hat_id)
            supplier_name = repo.suppliers.find(first_supplier).name
            repo.orders.insert(order)
            counter = counter + 1
            output1 = topping + ',' + supplier_name + ',' + location + "\n"
            file1 = open(sys.argv[3], 'a')
            file1.write(output1)
            file1.close()


if __name__ == '__main__':
    main()
