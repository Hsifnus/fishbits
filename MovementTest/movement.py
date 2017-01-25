from kivy.app import App
from kivy.uix.widget import Widget
from kivy.uix.image import Image
from kivy.uix.label import Label
from kivy.properties import NumericProperty, ReferenceListProperty,\
    ObjectProperty, BooleanProperty
from kivy.vector import Vector
from kivy.clock import Clock
from kivy.core.audio import SoundLoader

class Block(Image):
    block_id = NumericProperty(0)
    brightness = NumericProperty(1)
    is_solid = BooleanProperty(True)

    def __init__(self, bid, solid=True):
        super().__init__()
        self.block_id = bid
        self.brightness = 1
        self.is_solid = solid

class Player(Image):
    orientation = NumericProperty(0)
    health = NumericProperty(3)
    travel_speed = NumericProperty(4)
    velocity_x = NumericProperty(0)
    velocity_y = NumericProperty(0)
    velocity = ReferenceListProperty(velocity_x, velocity_y)

    def change_image(self):
        if self.is_still():
            if self.orientation == 0:
                self.source = 'kuri_idle_forward.gif'
            elif self.orientation == 1:
                self.source = 'kuri_idle_right.gif'
            elif self.orientation == 2:
                self.source = 'kuri_idle_back.gif'
            elif self.orientation == 3:
                self.source = 'kuri_idle_left.gif'
        else:
            if self.orientation == 0:
                self.source = 'kuri_run_forward.gif'
            elif self.orientation == 1:
                self.source = 'kuri_run_right.gif'
            elif self.orientation == 2:
                self.source = 'kuri_run_back.gif'
            elif self.orientation == 3:
                self.source = 'kuri_run_left.gif'

    def move(self):
        self.pos = Vector(*self.velocity) + self.pos

    def is_still(self):
        return self.velocity_x == 0 and self.velocity_y == 0

class MovementTest(Widget):
    target_pos = [0, 0]
    blocks, trajectory = [], []
    player = ObjectProperty(None)
    init_x = NumericProperty(15)
    init_y = NumericProperty(15)
    darkness_factor = NumericProperty(1)
    darkness_delta = NumericProperty(0.001)

    def init_player(self):
        self.player.velocity = (100, 0)
        self.player.move()
        self.player.velocity = (0, 0)

    def update(self, dt):
        if abs(self.player.center_x - self.target_pos[0]) < self.player.travel_speed and abs(self.player.center_y -
                                                                        self.target_pos[1]) < self.player.travel_speed:
            self.player.velocity = (0, 0)
        for block in self.blocks:
            if not self.player.is_still():
                if self.player.collide_widget(block) and (True in [block.center_x == pt[0] and
                                        block.center_y == pt[1] for pt in self.trajectory]) and block.is_solid:
                    self.player.velocity = (0, 0)
        self.player.change_image()
        self.player.move()
        self.check_darkness()
        if self.darkness_factor > 1 or self.darkness_factor < 0:
            self.darkness_delta *= -1
        self.darkness_factor += self.darkness_delta

    def check_darkness(self):
        def prox_factor(pos):
            distance = lambda p1, p2: ((p1[0]-p2[0])**2 + (p1[1]-p2[1])**2)**0.5
            if distance(pos, [self.player.center_x, self.player.center_y]) == 0:
                return 1
            else:
                return max(1 - 0.01 * ((self.darkness_factor + 0.01) / 1.01) * distance(pos, (self.player.center_x, self.player.center_y)), 0)
        for i in range(len(self.blocks)):
            block_pos = [self.blocks[i].center_x, self.blocks[i].center_y]
            self.blocks[i].brightness = prox_factor(block_pos)* (1 if self.blocks[i].is_solid else 0.8*prox_factor(block_pos))

    def generate_map(self, lst, solid=True):
        assert all([isinstance(item, list) for item in lst]), 'List must be two-dimensional.'
        for i in range(len(lst)):
            for j in range(len(lst[i])):
                if lst[i][j] != 0 and isinstance(lst[i][j], int):
                    new_block = Block(lst[i][j], solid)
                    new_block.pos = (32*j, 32*i)
                    self.blocks.append(new_block)
                    self.add_widget(new_block)
        self.check_darkness()
        
    def generate_background(self, lst, solid=False):
        assert all([isinstance(item, list) for item in lst]), 'List must be two-dimensional.'
        for i in range(len(lst)):
            for j in range(len(lst[i])):
                if lst[i][j] != 0 and isinstance(lst[i][j], int):
                    new_block = Block(lst[i][j], solid)
                    new_block.pos = (32*j, 32*i)
                    self.blocks.append(new_block)
                    self.add_widget(new_block)
        self.check_darkness()

    def points_between(self):
        point, point_list = list(self.player.center), []
        while point != self.target_pos:
            if self.player.orientation == 0:
                point = [point[0], point[1]-1]
            elif self.player.orientation == 1:
                point = [point[0] + 1, point[1]]
            elif self.player.orientation == 2:
                point = [point[0], point[1] + 1]
            elif self.player.orientation == 3:
                point = [point[0] - 1, point[1]]
            point_list.append(point)
            if len(point_list) > 10000:
                raise RecursionError("Too many points calculated in player trajectory")
        return [self.player.center] + point_list


    def on_touch_down(self, touch):
        if self.player.is_still():
            if abs(touch.x - self.player.center_x) < 16:
                if touch.y - self.player.center_y > 16:
                    self.player.orientation = 2  # facing back
                elif touch.y - self.player.center_y < -16:
                    self.player.orientation = 0  # facing front
            elif abs(touch.y - self.player.center_y) < 16:
                if touch.x - self.player.center_x > 16:
                    self.player.orientation = 1  # facing right
                elif touch.x - self.player.center_x < -16:
                    self.player.orientation = 3  # facing left

    def on_touch_move(self, touch):
        self.on_touch_down(touch)

    def on_touch_up(self, touch):
        if self.player.is_still():
            self.player.pos = (32 * (self.player.x // 32), 32 * (self.player.y // 32))
            if abs(touch.x - self.player.center_x) < 16:
                self.target_pos = [self.player.center_x, 32*(touch.y//32)+16]
                if touch.y - self.player.center_y > 16:
                    self.player.velocity = (0, self.player.travel_speed)
                    self.player.orientation = 2
                elif touch.y - self.player.center_y < -16:
                    self.player.velocity = (0, -self.player.travel_speed)
                    self.player.orientation = 0
                self.trajectory = self.points_between()
            elif abs(touch.y - self.player.center_y) < 16:
                self.target_pos = [32*(touch.x // 32) + 16, self.player.center_y]
                if touch.x - self.player.center_x > 16:
                    self.player.velocity = (self.player.travel_speed, 0)
                    self.player.orientation = 1
                elif touch.x - self.player.center_x < -16:
                    self.player.velocity = (-self.player.travel_speed, 0)
                    self.player.orientation = 3
                self.trajectory = self.points_between()

class MovementApp(App):
    def build(self):
        self.title = 'MovementTest'
        game = MovementTest()
        game.init_player()
        game.generate_background([
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        ])
        game.generate_map([
            [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
            [1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1],
            [1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1],
            [1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1],
            [1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1],
            [1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1],
            [1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1],
            [1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1],
            [1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1],
            [1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1],
            [1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1],
            [1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1],
            [1, 0, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1],
            [1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1],
            [1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1],
            [1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1],
            [1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1],
            [1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1],
            [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
        ])
        Clock.schedule_interval(game.update, 1.0 / 60.0)
        sound = SoundLoader.load('Crossing_the_Flora.wav')
        sound.loop = True
        sound.play()
        return game

if __name__ == '__main__':
    MovementApp().run()