function [x1,y1,w,h]=LabelBox(filename)   %使用鼠标标注图像位置并返回坐标（标注图像ROI） 在图像中勾选区域，结果
%显示输出在命令行并保存在ans中。返回值x1，y1为关键区域左上角坐标值，w,h为关键区域的宽和高。

Mat=imread(filename);
imshow(Mat);
mouse=drawrectangle;
pos=mouse.Position;
%pos=getPosition(mouse);% x1 y1 w h
%ROI=[fix(pos(1)) fix(pos(2)) fix(pos(1)+pos(3)) fix(pos(2)+pos(4))];
x1 = fix(pos(1));
y1= fix(pos(2));
width = fix(pos(3));
height = fix(pos(4));
w = width - mod(width,3);
h =  height - mod(height ,3);
end